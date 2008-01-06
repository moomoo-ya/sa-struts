/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.struts.action;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.customizer.ActionCustomizer;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.S2PropertyMessageResources;
import org.seasar.struts.util.S2PropertyMessageResourcesFactory;
import org.seasar.struts.validator.S2ValidatorPlugIn;

/**
 * @author higa
 * 
 */
public class ActionWrapperTest extends S2TestCase {

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    @Override
    public void setUpAfterContainerInit() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        S2ValidatorPlugIn plugIn = new S2ValidatorPlugIn();
        plugIn.setPathnames("validator-rules.xml,validation.xml");
        plugIn.init(new MyActionServlet(getServletContext()), moduleConfig);
        S2PropertyMessageResourcesFactory factory = new S2PropertyMessageResourcesFactory();
        S2PropertyMessageResources resources = new S2PropertyMessageResources(
                factory, "application");
        getServletContext().setAttribute(Globals.MESSAGES_KEY, resources);
        register(BbbAction.class, "bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testExecute() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("index");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, null,
                null, null, null);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/bbb/index.jsp", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate() throws Exception {
        register(CccAction.class, "cccAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("cccAction"));
        Method m = CccAction.class.getDeclaredMethod("execute");
        Method m2 = CccAction.class.getDeclaredMethod("validate");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, m2,
                SaveType.REQUEST, "/aaa/input.jsp", null);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/aaa/input.jsp", forward.getPath());
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
    }

    /**
     * @throws Exception
     */
    public void testExecute_validator() throws Exception {
        register(EeeAction.class, "aaa_eeeAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_eeeAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/eee");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping.findExecuteConfig(
                getRequest(), ""));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/aaa/input.jsp", forward.getPath());
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate_session() throws Exception {
        register(DddAction.class, "dddAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("dddAction"));
        Method m = DddAction.class.getDeclaredMethod("execute");
        Method m2 = DddAction.class.getDeclaredMethod("validate");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, m2,
                SaveType.SESSION, "/aaa/input.jsp", null);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/aaa/input.jsp", forward.getPath());
        assertNotNull(getRequest().getSession().getAttribute(Globals.ERROR_KEY));
    }

    /**
     * @throws Exception
     */
    public void testExportPropertiesToRequest() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("index");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, null,
                null, null, null);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertEquals("111", getRequest().getAttribute("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testExportPropertiesToRequest_actionAndForm() throws Exception {
        register(FffAction.class, "fffAction");
        register(MyForm.class, "myForm");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("fffAction"));
        Method m = FffAction.class.getDeclaredMethod("execute");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, null,
                null, null, null);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        actionMapping.setActionFormPropertyDesc(actionMapping
                .getActionBeanDesc().getPropertyDesc("myForm"));
        FffAction action = (FffAction) getComponent("fffAction");
        action.hoge = "111";
        action.myForm.aaa = "222";
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertEquals("111", getRequest().getAttribute("hoge"));
        assertEquals("222", getRequest().getAttribute("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testValidate_validator() throws Exception {
        register(EeeAction.class, "aaa_eeeAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_eeeAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/eee");
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionMessages errors = wrapper.validate(getRequest(), actionMapping
                .getExecuteConfig("execute"));
        System.out.println(errors);
        assertFalse(errors.isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testInputForward() throws Exception {
        register(GggAction.class, "aaa_gggAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_gggAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/ggg");
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionForward forward = wrapper.execute(getRequest(), actionMapping
                .getExecuteConfig("execute"));
        assertEquals("/aaa/input2.jsp", forward.getPath());
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        public String hoge;

        /**
         * @return
         */
        public String index() {
            hoge = "111";
            return "index.jsp";
        }

        /**
         * @return
         */
        public String execute2() {
            return "execute2.jsp";
        }
    }

    /**
     * 
     */
    public static class CccAction {

        /**
         * @return
         */
        @Execute(validate = "validate")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class DddAction {

        /**
         * @return
         */
        @Execute(validate = "validate", saveErrors = SaveType.SESSION, input = "/aaa/input.jsp")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class EeeAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(input = "/aaa/input.jsp")
        public String execute() {
            return "success";
        }
    }

    /**
     * 
     */
    public static class FffAction {

        /**
         * 
         */
        @ActionForm
        public MyForm myForm;

        /**
         * 
         */
        public String hoge;

        /**
         * @return
         */
        @Execute
        public String execute() {
            return "/aaa/bbb.jsp";
        }
    }

    /**
     * 
     */
    public static class GggAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(input = "/aaa/input2.jsp")
        public String execute() {
            return "success";
        }
    }

    private static class MyActionServlet extends ActionServlet {
        private static final long serialVersionUID = 1L;

        private ServletContext servletContext;

        /**
         * @param servletContext
         */
        public MyActionServlet(ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }
    }

    /**
     * 
     */
    public static class MyForm {
        /**
         * 
         */
        public String aaa;
    }
}