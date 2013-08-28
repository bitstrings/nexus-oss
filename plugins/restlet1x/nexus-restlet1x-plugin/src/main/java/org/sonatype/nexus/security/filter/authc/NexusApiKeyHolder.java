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

package org.sonatype.nexus.security.filter.authc;

import java.util.Set;

/**
 * Marker interface for API-Keys; Unlike {@link NexusApiKey}, here the component hint is not used, but instead the
 * return value of the {@link #getHttpHeaders()} method. This makes runtime changes more easier (than registering new
 * component with new hint, as would happen in old Plexus).
 * 
 * @since 2.7.0
 */
public interface NexusApiKeyHolder
{
  /**
   * Returns the set of HTTP headers that should be considered as API-Keys, never {@code null}.
   */
  Set<String> getHttpHeaders();
}
