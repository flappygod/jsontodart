package com.flappygo.jsontodart.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

}
