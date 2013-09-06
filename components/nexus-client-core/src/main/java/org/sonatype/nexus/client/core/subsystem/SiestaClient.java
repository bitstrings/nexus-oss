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

package org.sonatype.nexus.client.core.subsystem;

import org.sonatype.sisu.siesta.client.Filters;
import org.sonatype.sisu.siesta.client.filters.ErrorsV1JsonFilter;
import org.sonatype.sisu.siesta.client.filters.ErrorsV1XmlFilter;
import org.sonatype.sisu.siesta.client.filters.ValidationErrorsV1JsonFilter;
import org.sonatype.sisu.siesta.client.filters.ValidationErrorsV1XmlFilter;

/**
 * Base interface for clients accessing a siesta endpoint.
 *
 * @since 2.7
 */
@Filters({
    ErrorsV1JsonFilter.class,
    ErrorsV1XmlFilter.class,
    ValidationErrorsV1JsonFilter.class,
    ValidationErrorsV1XmlFilter.class
})
public interface SiestaClient
{

}
