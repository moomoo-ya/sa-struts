/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.struts.util;

import org.apache.struts.Globals;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.config.S2ActionMapping;

/**
 * @author higa
 * 
 */
public class S2ActionMappingUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetActionMapping() throws Exception {
        getRequest().setAttribute(Globals.MAPPING_KEY, new S2ActionMapping());
        assertNotNull(S2ActionMappingUtil.getActionMapping());
    }
}