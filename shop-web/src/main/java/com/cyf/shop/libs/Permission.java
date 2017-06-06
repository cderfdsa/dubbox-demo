package com.cyf.shop.libs;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.center.common.business.service.CacheService;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.Node;
import com.cyf.shop.auth.service.NodeService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyf on 2017/3/26.
 */
@Component
public class Permission {
    private final static long SUPER_ADMIN_USER_ID = 1L;//超级管理员的用户id
    private final static String USER_NODE_CACHE_PREFIX = "user_node_cache_";
    @Reference
    CacheService cacheService;
    @Reference
    NodeService nodeService;

    /**
     * 判断用户是否具有访问指定路由的权限
     * @param userId
     * @param path
     * @return
     */
    public boolean isPermit(Long userId, String path){
        if(isSuperAdmin(userId)){
            return true;
        }else if (getNodeMap(userId).containsKey(path)) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 取得用户能够访问的所有节点
     * @param userId
     * @return
     */
    public List<Node> getUserNodes(Long userId){
        List<Node> nodeList;
        //如果用户已登陆，则尝试从缓存中取得，如果缓存中已经有，则直接从缓存中获取并返回，如果缓存中没有，则从数据库中获取
        if(userId != null && userId > 0){
            String jsonStr = cacheService.get(getCacheKey(userId));
            if(! StringUtil.isEmpty(jsonStr)){
                nodeList = JsonUtil.toList(jsonStr, Node.class);
                return nodeList;
            }
        }

        if(isSuperAdmin(userId)){//如果是超级管理员，则直接取得所有节点
            nodeList = nodeService.listAllActiveNode("sort desc");
        }else{
            nodeList = nodeService.listActiveAuthNodeByUserId(userId);
        }

        if(nodeList != null && nodeList.size() > 0){
            //取得之后，存到缓存中去
            String jsonStr = JsonUtil.toStringWithNull(nodeList);
            cacheService.set(getCacheKey(userId), jsonStr, 30 * 60 * 1000);//默认缓存30分钟
        }
        return nodeList;
    }

    /**
     * 取得当前用户当前节点下的所有子节点
     * @param userId
     * @param nodeId
     * @return
     */
    public List<Node> getUserChildNode(Long userId, Long nodeId){
        List<Node> nodeList = nodeService.listAllActiveChildNode(nodeId);
        //过滤掉当前用户不拥有的权限
        if(! nodeList.isEmpty()){
            Map<Long, String> nodeMap = getNodeIdMap(userId);
            Iterator<Node> iterator = nodeList.iterator();
            Node node;
            while (iterator.hasNext()){
                node = iterator.next();
                if(! nodeMap.containsKey(node.getId())){
                    iterator.remove();
                }
            }
        }
        return nodeList;
    }

    private Map<Long, String> getNodeIdMap(Long userId){
        Map<Long, String> nodeMap = new HashMap<>();

        List<Node> nodeList = getUserNodes(userId);
        if(nodeList != null && ! nodeList.isEmpty()){
            for(Node node : nodeList){
                nodeMap.put(node.getId(), node.getName());
            }
        }
        return nodeMap;
    }

    /**
     * 取得当前用户可访问的所有节点的Map，key:value = url:name
     * @param userId
     * @return
     */
    public Map<String, String> getNodeMap(Long userId){
        Map<String, String> nodeMap = new HashMap<>();

        List<Node> nodeList = getUserNodes(userId);
        if(nodeList != null && ! nodeList.isEmpty()){
            for(Node node : nodeList){
                nodeMap.put(node.getUrl(), node.getName());
            }
        }
        return nodeMap;
    }

    /**
     * 判断某个用户是否是超级管理员
     * @param userId
     * @return
     */
    public boolean isSuperAdmin(Long userId){
        if(userId != null && userId.equals(SUPER_ADMIN_USER_ID)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 清除节点缓存
     * @param userId
     */
    public boolean clearCacheNode(Long userId){
        return cacheService.del(getCacheKey(userId));
    }

    private String getCacheKey(Long userId){
        return USER_NODE_CACHE_PREFIX + userId;
    }
}
