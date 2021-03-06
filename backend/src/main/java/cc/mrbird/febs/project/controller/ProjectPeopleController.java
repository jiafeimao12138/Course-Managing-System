package cc.mrbird.febs.project.controller;


import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.project.domain.PeopleInGroup;
import cc.mrbird.febs.project.domain.ProjectInfo;
import cc.mrbird.febs.project.domain.ProjectPeople;
import cc.mrbird.febs.project.domain.TUserInfo;
import cc.mrbird.febs.project.service.ProjectPeopleService;
import cc.mrbird.febs.project.service.TUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author hyl
 */
@Slf4j
@RestController
@RequestMapping("project")
public class ProjectPeopleController {
    @Autowired
    private ProjectPeopleService projectPeopleService;

    @Autowired
    private TUserInfoService tUserInfoService;

    private String message;

    String getUsername(){
        String username="";
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotBlank(token)) {
            username = JWTUtil.getUsername(token);
        }
        return username;
    }

//    @GetMapping("details")
//    public FebsResponse getProjectDetail(@RequestParam(name="pid") String sid){
//        List<ProjectPeople> list =  this.projectPeopleService.findBySid(sid);
//        return new FebsResponse().code("200").message("请求成功").status("success").data(list);
//    }


    @GetMapping("same_group")
    public FebsResponse getPeopleInSameGroup(@RequestParam(name="pid") String pid) {
        TUserInfo userInfo= tUserInfoService.findByUsername(this.getUsername());
        String sid= userInfo.getSid();
        List<PeopleInGroup> list = this.projectPeopleService.getAllPeopleInGroup(sid,pid);
        if(list.isEmpty()){
            return new FebsResponse().code("404").message("not found").status("not found");
        }
        return new FebsResponse().code("200").message("请求成功").status("success").data(list);
    }

    @PostMapping("details")
    public FebsResponse addProjectPeople(@RequestBody List<ProjectPeople> projectPeoples) throws FebsException{
        try {
            //TODO 转换
            this.projectPeopleService.createProjectPeoples(projectPeoples);
            return new FebsResponse().code("200").message("新增项目信息成功").status("success");
        } catch (Exception e) {
            message = "新增项目信息失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
