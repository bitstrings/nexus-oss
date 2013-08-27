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
/*
 =================== DO NOT EDIT THIS FILE ====================
 Generated by Modello 1.3 on 2010-10-25 11:16:11,
 any modifications will be overwritten.
 ==============================================================
 */

package org.sonatype.nexus.configuration.model.v1_4_5.upgrade;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/


/**
 * Converts from version 1.4.5 (with version in package name) to
 * version 1.4.5 (without version in package name) of the model.
 *
 * @version $Revision: 7811 $ $Date: 2011-02-02 11:22:58 -0600 (Wed, 02 Feb 2011) $
 */
@SuppressWarnings("all")
public interface VersionConverter
{

  //-----------/
  // - Methods -/
  // -----------/

  /**
   * @param cErrorReporting
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CErrorReporting convertCErrorReporting(
      org.sonatype.nexus.configuration.model.v1_4_4.CErrorReporting cErrorReporting);

  /**
   * @param cHttpProxySettings
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CHttpProxySettings convertCHttpProxySettings(
      org.sonatype.nexus.configuration.model.v1_4_4.CHttpProxySettings cHttpProxySettings);

  /**
   * @param cLocalStorage
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CLocalStorage convertCLocalStorage(
      org.sonatype.nexus.configuration.model.v1_4_4.CLocalStorage cLocalStorage);

  /**
   * @param cMirror
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CMirror convertCMirror(
      org.sonatype.nexus.configuration.model.v1_4_4.CMirror cMirror);

  /**
   * @param cNotification
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CNotification convertCNotification(
      org.sonatype.nexus.configuration.model.v1_4_4.CNotification cNotification);

  /**
   * @param cNotificationTarget
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CNotificationTarget convertCNotificationTarget(
      org.sonatype.nexus.configuration.model.v1_4_4.CNotificationTarget cNotificationTarget);

  /**
   * @param cPathMappingItem
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CPathMappingItem convertCPathMappingItem(
      org.sonatype.nexus.configuration.model.v1_4_4.CPathMappingItem cPathMappingItem);

  /**
   * @param cPlugin
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CPlugin convertCPlugin(
      org.sonatype.nexus.configuration.model.v1_4_4.CPlugin cPlugin);

  /**
   * @param cProps
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CProps convertCProps(
      org.sonatype.nexus.configuration.model.v1_4_4.CProps cProps);

  /**
   * @param cRemoteAuthentication
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRemoteAuthentication convertCRemoteAuthentication(
      org.sonatype.nexus.configuration.model.v1_4_4.CRemoteAuthentication cRemoteAuthentication);

  /**
   * @param cRemoteConnectionSettings
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRemoteConnectionSettings convertCRemoteConnectionSettings(
      org.sonatype.nexus.configuration.model.v1_4_4.CRemoteConnectionSettings cRemoteConnectionSettings);

  /**
   * @param cRemoteHttpProxySettings
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRemoteHttpProxySettings convertCRemoteHttpProxySettings(
      org.sonatype.nexus.configuration.model.v1_4_4.CRemoteHttpProxySettings cRemoteHttpProxySettings);

  /**
   * @param cRemoteNexusInstance
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRemoteNexusInstance convertCRemoteNexusInstance(
      org.sonatype.nexus.configuration.model.v1_4_4.CRemoteNexusInstance cRemoteNexusInstance);

  /**
   * @param cRemoteStorage
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRemoteStorage convertCRemoteStorage(
      org.sonatype.nexus.configuration.model.v1_4_4.CRemoteStorage cRemoteStorage);

  /**
   * @param cRepository
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRepository convertCRepository(
      org.sonatype.nexus.configuration.model.v1_4_4.CRepository cRepository);

  /**
   * @param cRepositoryGrouping
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRepositoryGrouping convertCRepositoryGrouping(
      org.sonatype.nexus.configuration.model.v1_4_4.CRepositoryGrouping cRepositoryGrouping);

  /**
   * @param cRepositoryTarget
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRepositoryTarget convertCRepositoryTarget(
      org.sonatype.nexus.configuration.model.v1_4_4.CRepositoryTarget cRepositoryTarget);

  /**
   * @param cRestApiSettings
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRestApiSettings convertCRestApiSettings(
      org.sonatype.nexus.configuration.model.v1_4_4.CRestApiSettings cRestApiSettings);

  /**
   * @param cRouting
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CRouting convertCRouting(
      org.sonatype.nexus.configuration.model.v1_4_4.CRouting cRouting);

  /**
   * @param cScheduleConfig
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CScheduleConfig convertCScheduleConfig(
      org.sonatype.nexus.configuration.model.v1_4_4.CScheduleConfig cScheduleConfig);

  /**
   * @param cScheduledTask
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CScheduledTask convertCScheduledTask(
      org.sonatype.nexus.configuration.model.v1_4_4.CScheduledTask cScheduledTask);

  /**
   * @param cSmtpConfiguration
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.CSmtpConfiguration convertCSmtpConfiguration(
      org.sonatype.nexus.configuration.model.v1_4_4.CSmtpConfiguration cSmtpConfiguration);

  /**
   * @param configuration
   */
  public org.sonatype.nexus.configuration.model.v1_4_5.Configuration convertConfiguration(
      org.sonatype.nexus.configuration.model.v1_4_4.Configuration configuration);
}
