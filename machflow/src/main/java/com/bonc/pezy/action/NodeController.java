package com.bonc.pezy.action;

import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.NodeService;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.vo.NodeVo;
import com.bonc.pezy.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("node")
@Api(value = "组件信息", description = "组件的管理")
public class NodeController {

    private final Logger logger = LoggerFactory.getLogger(NodeController.class);
    @Autowired
    private NodeService nodeService;

    @ApiOperation(value = "查找所有的组件信息", httpMethod = "GET")
    @RequestMapping(value = "/getAllNodes", method = RequestMethod.GET)
    @ResponseBody
    public Result getAllNodes() {
        List<Node> allNodes = nodeService.findAll();
        Result result = ResultUtil.success(allNodes);
        return result;
    }

    @ApiOperation(value = "删除组件（id）", httpMethod = "POST")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestParam(value = "nodeId") Long nodeId) {
        nodeService.deleteById(nodeId);
        Result result = ResultUtil.success();
        return result;
    }

    //增加节点
    @ApiOperation(value = "增加node",httpMethod = "POST")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result register(@Validated NodeVo nodeVo) throws Exception{
        Node node = new Node();
        BeanUtils.copyProperties(nodeVo,node);
        nodeService.save(node);
        return ResultUtil.success();
    }
}
