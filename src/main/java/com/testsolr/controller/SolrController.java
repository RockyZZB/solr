package com.testsolr.controller;/*
 *@ClassName: SolrController
 *@Description: TODO
 *@author: 郑波
 *@date: 2019-03-10 23:04
 *@version: 1.0
 */

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/solr")
public class SolrController {

    @Autowired
    private SolrClient solrClient;

    @RequestMapping("/add")
    public String add(){
        String uuid=UUID.randomUUID().toString().replaceAll("-","");
        try {
            SolrInputDocument doc=new SolrInputDocument();
            doc.setField("id",uuid);
            doc.setField("content_ik","我是中国人");
            solrClient.add(doc);
            return  uuid;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    @RequestMapping("/get/{id}")
    public String get(@PathVariable String id) throws Exception {
        SolrDocument doc=solrClient.getById(id);
        System.out.println(doc);
        return doc.toString();
    }

    @RequestMapping("/find/{q}{page}{size}")
    public Map<String,Object> find(@PathVariable String q,@PathVariable Integer page,@PathVariable Integer size) throws Exception{
        SolrQuery params=new SolrQuery();
        params.set("q",q);
        params.addSort("id",SolrQuery.ORDER.desc);
        params.setStart(page);
        params.setRows(size);
        params.set("df","text");
        params.set("fl","id");
        params.setHighlight(true);
        params.setHighlightSimplePre("<span style='color:red'>");
        params.setHighlightSimplePost("</span>");
        QueryResponse query = solrClient.query(params);
        SolrDocumentList results = query.getResults();
        Long total=results.getNumFound();

        Map<String,Map<String,List<String>>> highlight=query.getHighlighting();
        Map<String,Object> map=new HashMap<>();
        map.put("total",total);
        map.put("data",highlight);
        return  map;


    }

}
