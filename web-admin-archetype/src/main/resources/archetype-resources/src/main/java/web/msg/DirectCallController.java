#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright (C) 2015
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ${package}.web.msg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import javax.annotation.Nullable;

import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.directcall.DirectCall;
import org.cleverbus.core.common.directcall.DirectCallParams;
import org.cleverbus.core.common.directcall.DirectCallRegistry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for direct call form.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Controller
public class DirectCallController {

    private static final String VIEW_NAME = "externalCall";
    private static final String CALL_RESULT_ATTR = "callResult";
    private static final String CALL_RESULT_ERR_ATTR = "callResultError";

    @Autowired
    private DirectCallRegistry callRegistry;

    @Autowired
    private DirectCall directCall;

    @RequestMapping(value = "/directCall", method = RequestMethod.GET)
    public String showForm(@ModelAttribute("model") ModelMap modelMap) {
        return VIEW_NAME;
    }

    @RequestMapping(value = "/directCall", method = RequestMethod.POST)
    public String processCallForm(@ModelAttribute("model") ModelMap modelMap, @RequestParam("body") String body,
            @RequestParam("uri") String uri, @RequestParam("senderRef") String senderRef,
            @RequestParam("soapAction") @Nullable String soapAction, @RequestParam("header") String header) {

        Assert.hasText(body, "the body must not be empty");
        Assert.hasText(uri, "the uri must not be empty");
        Assert.hasText(senderRef, "the senderRef must not be empty");

        // generate unique ID
        String callId = UUID.randomUUID().toString();

        // save params into registry
        DirectCallParams params = new DirectCallParams(body, uri, senderRef, soapAction, StringUtils.trimToNull(header));
        callRegistry.addParams(callId, params);

        // call external system via internal servlet route
        try {
            Log.debug("Calling external system with callId=" + callId + ", params: " + params);

            String res = directCall.makeCall(callId);

            modelMap.put(CALL_RESULT_ATTR, MessageController.prettyPrintXML(res));
        } catch (Exception ex) {
            // error occurred
            StringWriter strOut = new StringWriter();
            PrintWriter resStr = new PrintWriter(strOut);
            ExceptionUtils.printRootCauseStackTrace(ex, resStr);

            modelMap.put(CALL_RESULT_ERR_ATTR, strOut.toString());
        }

        modelMap.put("header", header);
        modelMap.put("body", body);
        modelMap.put("uri", uri);
        modelMap.put("senderRef", senderRef);
        modelMap.put("soapAction", soapAction);

        return VIEW_NAME;
    }
}
