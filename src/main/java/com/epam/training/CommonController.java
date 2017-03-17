package com.epam.training;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CommonController {

    @RequestMapping("/")
    public String getIndexPage() {
        return "/static/index.html";
    }

    @RequestMapping("/rootFiles")
    @ResponseBody
    public List<File> getRootFiles() {
        List<File> fileList = new ArrayList<File>();
        File[] fileArray = File.listRoots();
        for (File file : fileArray) fileList.add(file);
        return fileList;
    }

    @RequestMapping("/chosenFile")
    @ResponseBody
    public List<File> getChosenFile(@RequestParam String path) {
        List<File> fileList = new ArrayList<File>();
        File chosenFile = new File(path);
        if (chosenFile.isDirectory())
            for (File file : chosenFile.listFiles()) fileList.add(file);
        return fileList;
    }
}
