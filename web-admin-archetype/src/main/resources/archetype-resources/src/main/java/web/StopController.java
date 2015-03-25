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

package ${package}.web;

import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.common.asynch.stop.StopService;
import org.cleverbus.spi.msg.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller for stopping ESB.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Controller
public class StopController {

    private static final String VIEW_NAME = "stop";

    @Autowired
    private StopService stopService;

    @Autowired
    private MessageService messageService;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getStoppingState(ModelMap model) {
        addStoppingState(model);

        if (stopService.isStopping()) {
            addMsgCounts(model);
        }

        return VIEW_NAME;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public String stopEsb(ModelMap model) {
        stopService.stop();

        addStoppingState(model);
        addMsgCounts(model);

        return VIEW_NAME;
    }

    @RequestMapping(value = "/cancelStop", method = RequestMethod.POST)
    public String cancelStopEsb(ModelMap model) {
        stopService.cancelStopping();

        addStoppingState(model);

        return VIEW_NAME;
    }

    private void addStoppingState(ModelMap model) {
        model.addAttribute("isStopping", stopService.isStopping());
    }

    private void addMsgCounts(ModelMap model) {
        model.addAttribute("processingCount", messageService.getCountMessages(MsgStateEnum.PROCESSING, null));
        model.addAttribute("waitingForResCount", messageService.getCountMessages(MsgStateEnum.WAITING_FOR_RES, null));
    }
}
