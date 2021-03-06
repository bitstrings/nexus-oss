/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.nexus.plugins.p2.repository.metadata;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sonatype.nexus.logging.AbstractLoggingComponent;
import org.sonatype.nexus.plugins.p2.repository.P2Constants;
import org.sonatype.nexus.plugins.p2.repository.P2Repository;
import org.sonatype.nexus.plugins.p2.repository.proxy.P2RuntimeExceptionMaskedAsINFException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.RemoteStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.access.Action;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.DefaultStorageFileItem;
import org.sonatype.nexus.proxy.item.FileContentLocator;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.RepositoryItemUidLock;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;
import org.sonatype.nexus.proxy.utils.RepositoryStringUtils;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.MXSerializer;

public abstract class AbstractP2MetadataSource<E extends P2Repository>
    extends AbstractLoggingComponent
    implements P2MetadataSource<E>
{

  protected static final List<String> METADATA_PATHS = Arrays.asList(P2Constants.SITE_XML, P2Constants.CONTENT_JAR,
      P2Constants.CONTENT_XML, P2Constants.ARTIFACTS_JAR, P2Constants.ARTIFACTS_XML,
      P2Constants.COMPOSITE_CONTENT_XML, P2Constants.COMPOSITE_CONTENT_JAR, P2Constants.COMPOSITE_ARTIFACTS_XML,
      P2Constants.COMPOSITE_ARTIFACTS_JAR, P2Constants.P2_INDEX);

  private StorageFileItem cacheMetadataItem(final StorageFileItem result, final Map<String, Object> context,
                                            final E repository)
      throws IOException
  {
    setItemAttributes(result, context, repository);
    getLogger().debug("Repository " + repository.getId() + ": Created metadata item " + result.getName());
    return doCacheItem(result, repository);
  }

  protected static StorageFileItem createMetadataItem(final Repository repository, final String path,
                                                      final Xpp3Dom metadata, final String hack,
                                                      final Map<String, Object> context)
      throws IOException
  {
    // this is a special one: once cached (hence consumed), temp file get's deleted
    final FileContentLocator fileContentLocator = new FileContentLocator("text/xml");
    OutputStream buffer = null;
    try {
      buffer = fileContentLocator.getOutputStream();
      final MXSerializer mx = new MXSerializer();
      mx.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      mx.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      final String encoding = "UTF-8";
      mx.setOutput(buffer, encoding);
      mx.startDocument(encoding, null);
      if (hack != null) {
        mx.processingInstruction(hack);
      }
      metadata.writeToSerializer(null, mx);
      mx.flush();
    }
    finally {
      IOUtil.close(buffer);
    }

    final DefaultStorageFileItem result =
        new DefaultStorageFileItem(repository, new ResourceStoreRequest(path), true /* isReadable */,
            false /* isWritable */, fileContentLocator);
    result.getItemContext().putAll(context);
    result.setLength(fileContentLocator.getLength());
    return result;
  }

  protected void setItemAttributes(final StorageFileItem item, final Map<String, Object> context, final E repository) {
    // this is a hook, do nothing by default
  }

  protected StorageFileItem doCacheItem(final StorageFileItem item, final E repository)
      throws LocalStorageException
  {
    StorageFileItem result = null;

    try {
      final RepositoryItemUidLock itemLock = item.getRepositoryItemUid().getLock();
      itemLock.lock(Action.create);
      try {
        repository.getLocalStorage().storeItem(repository, item);
        repository.removeFromNotFoundCache(item.getResourceStoreRequest());
        result =
            (StorageFileItem) repository.getLocalStorage().retrieveItem(repository,
                new ResourceStoreRequest(item));
      }
      finally {
        itemLock.unlock();
      }

      result.getItemContext().putAll(item.getItemContext());
    }
    catch (ItemNotFoundException ex) {
      getLogger().warn(
          "Nexus BUG in "
              + RepositoryStringUtils.getHumanizedNameString(repository)
              + ", ItemNotFoundException during cache! Please report this issue along with the stack trace below!",
          ex);

      // this is a nonsense, we just stored it!
      result = item;
    }
    catch (UnsupportedStorageOperationException ex) {
      getLogger().warn(
          "LocalStorage or repository " + RepositoryStringUtils.getHumanizedNameString(repository)
              + " does not handle STORE operation, not caching remote fetched item.", ex);

      result = item;
    }

    return result;
  }

  @Override
  public StorageItem doRetrieveItem(final ResourceStoreRequest request, final E repository)
      throws IOException, ItemNotFoundException
  {
    if (!isP2MetadataItem(request.getRequestPath())) {
      // let real resource store retrieve the item
      return null;
    }

    final long start = System.currentTimeMillis();

    // because we are outside realm of nexus here, we need to handle locking ourselves...
    final RepositoryItemUid repoUid = repository.createUid(P2Constants.METADATA_LOCK_PATH);
    final RepositoryItemUidLock repoLock = repoUid.getLock();

    // needed to give away the lock on actual metadata file (content.xml or artifact.xml) as we will regenerate it
    final RepositoryItemUid itemUid = repository.createUid(request.getRequestPath());
    final RepositoryItemUidLock itemLock = itemUid.getLock();

    // start with read lock, no need to do a write lock until we find it necessary
    try {
      repoLock.lock(Action.read);
      if (P2Constants.CONTENT_PATH.equals(request.getRequestPath())) {
        try {
          final AbstractStorageItem contentItem = doRetrieveLocalItem(request, repository);
          if (request.isRequestLocalOnly() || !isContentOld(contentItem, repository)) {
            return contentItem;
          }
        }
        catch (final ItemNotFoundException e) {
          // fall through
        }

        // give away the lock on content.xml as we will regenerate it
        itemLock.unlock();
        try {
          // we need to get new file, so update the lock
          repoLock.lock(Action.delete);
          // recheck the condition now that we have an exclusive lock
          try {
            final AbstractStorageItem contentItem = doRetrieveLocalItem(request, repository);
            if (!isContentOld(contentItem, repository)) {
              return contentItem;
            }
          }
          catch (final ItemNotFoundException e) {
            // fall through
          }

          try {
            final StorageItem result = doRetrieveContentItem(request.getRequestContext(), repository);
            doRetrieveArtifactsItem(request.getRequestContext(), repository);
            return result;
          }
          catch (final P2RuntimeExceptionMaskedAsINFException e) {
            return doRetrieveLocalOnTransferError(request, repository, e);
          }
        }
        finally {
          // release repo
          repoLock.unlock();
          // get back the lock we gave in
          itemLock.lock(Action.read);
        }
      }
      else if (P2Constants.ARTIFACTS_PATH.equals(request.getRequestPath())) {
        try {
          final AbstractStorageItem artifactsItem = doRetrieveLocalItem(request, repository);
          if (request.isRequestLocalOnly() || !isArtifactsOld(artifactsItem, repository)) {
            return artifactsItem;
          }
        }
        catch (final ItemNotFoundException e) {
          // fall through
        }

        // give away the lock on artifacts.xml as we will regenerate it
        itemLock.unlock();
        try {
          // we need to get new file, so update the lock
          repoLock.lock(Action.delete);
          // recheck the condition now that we have an exclusive lock
          try {
            final AbstractStorageItem artifactsItem = doRetrieveLocalItem(request, repository);
            if (!isArtifactsOld(artifactsItem, repository)) {
              return artifactsItem;
            }
          }
          catch (final ItemNotFoundException e) {
            // fall through
          }

          try {
            deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.PRIVATE_ROOT));
            doRetrieveContentItem(request.getRequestContext(), repository);
            return doRetrieveArtifactsItem(request.getRequestContext(), repository);
          }
          catch (final P2RuntimeExceptionMaskedAsINFException e) {
            return doRetrieveLocalOnTransferError(request, repository, e);

          }
        }
        finally {
          // release repo
          repoLock.unlock();
          // get back the lock we gave in
          itemLock.lock(Action.read);
        }
      }

      // we explicitly do not serve any other metadata files
      throw new ItemNotFoundException(request, repository);
    }
    finally {
      // release repo read lock we initially acquired
      repoLock.unlock();

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "Repository " + repository.getId() + ": retrieve item: " + request.getRequestPath() + ": took "
                + (System.currentTimeMillis() - start) + " ms.");
      }
    }
  }

  /**
   * If the given RuntimeException turns out to be a P2 server error, try to retrieve the item locally, else rethrow
   * exception.
   */
  private StorageItem doRetrieveLocalOnTransferError(final ResourceStoreRequest request, final E repository,
                                                     final P2RuntimeExceptionMaskedAsINFException e)
      throws LocalStorageException, ItemNotFoundException
  {
    final Throwable cause = e.getCause().getCause();
    // TODO This must be possible to be done in some other way
    if (cause != null
        && (cause.getMessage().startsWith("HTTP Server 'Service Unavailable'") ||
        cause.getCause() instanceof ConnectException)) {
      // P2.getRemoteRepositoryItem server error
      return doRetrieveLocalItem(request, repository);
    }
    throw e;
  }

  public static boolean isP2MetadataItem(final String path) {
    return METADATA_PATHS.contains(path);
  }

  private void deleteP2Metadata(final Repository repository) {
    getLogger().debug("Repository " + repository.getId() + ": Deleting p2 metadata items.");
    deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.ARTIFACTS_JAR));
    deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.ARTIFACTS_XML));
    deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.CONTENT_JAR));
    deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.CONTENT_XML));
    deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.PRIVATE_ROOT));
    getLogger().debug("Repository " + repository.getId() + ": Deleted p2 metadata items.");
  }

  private static void deleteItemSilently(final Repository repository, final ResourceStoreRequest request) {
    try {
      repository.getLocalStorage().deleteItem(repository, request);
    }
    catch (final Exception e) {
      // that's okay, darling, don't worry about this too much
    }
  }

  protected AbstractStorageItem doRetrieveLocalItem(final ResourceStoreRequest request, final E repository)
      throws LocalStorageException, ItemNotFoundException
  {
    if (repository.getLocalStorage() != null) {
      final AbstractStorageItem localItem = repository.getLocalStorage().retrieveItem(repository, request);
      localItem.getItemContext().putAll(request.getRequestContext());
      return localItem;
    }
    throw new ItemNotFoundException(request, repository);
  }

  protected StorageItem doRetrieveArtifactsItem(final Map<String, Object> context, final E repository)
      throws IOException, ItemNotFoundException
  {
    final StorageFileItem fileItem = doRetrieveArtifactsFileItem(context, repository);
    try {
      getLogger().debug("Repository " + repository.getId() + ": Deleting p2 artifacts metadata items.");
      deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.ARTIFACTS_JAR));
      deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.ARTIFACTS_XML));
      getLogger().debug("Repository " + repository.getId() + ": Deleted p2 artifacts metadata items.");

      return cacheMetadataItem(fileItem, context, repository);
    }
    catch (final IOException e) {
      throw new LocalStorageException(e.getMessage(), e);
    }
  }

  protected StorageItem doRetrieveContentItem(final Map<String, Object> context, final E repository)
      throws IOException, ItemNotFoundException
  {
    final StorageFileItem fileItem = doRetrieveContentFileItem(context, repository);
    try {
      getLogger().debug("Repository " + repository.getId() + ": Deleting p2 content metadata items.");
      deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.CONTENT_JAR));
      deleteItemSilently(repository, new ResourceStoreRequest(P2Constants.CONTENT_XML));
      getLogger().debug("Repository " + repository.getId() + ": Deleted p2 content metadata items.");

      return cacheMetadataItem(fileItem, context, repository);
    }
    catch (final IOException e) {
      throw new LocalStorageException(e.getMessage(), e);
    }
  }

  protected abstract StorageFileItem doRetrieveArtifactsFileItem(Map<String, Object> context, E repository)
      throws RemoteStorageException, ItemNotFoundException;

  protected abstract StorageFileItem doRetrieveContentFileItem(Map<String, Object> context, E repository)
      throws RemoteStorageException, ItemNotFoundException;

  protected abstract boolean isArtifactsOld(AbstractStorageItem artifactsItem, E repository);

  protected abstract boolean isContentOld(AbstractStorageItem contentItem, E repository);
}
