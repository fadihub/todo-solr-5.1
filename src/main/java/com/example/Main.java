package com.example;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by fadi on 5/13/15.
 */
public class Main {
    public static void main(String[] args) {
        try {
            test();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void test() throws Exception {
        SolrClient server = new HttpSolrClient("http://localhost:8983/solr/todoitem");

        // Creating a doc manually.
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "123456789" );
        document.addField("name", "Mac computer" );
        document.addField("price", 1200 );

        //Creating 2 Todoitem objects and printing them.
        TodoItem item = new TodoItem("George", "finish your homework", "school");
        TodoItem  item2 = new TodoItem("John", "Buy a computer", "Shopping");
        System.out.println("Item1: \n" + item);
        System.out.println("\nItem2: \n" +item2 +"\n");

        //Adding the document and the 2 todoitems to the solr server.
        /*Note we were able to add the items object as beans becasue we added the @Field annotation on the fields as you can
        in the TodoItem class*/

        server.add(document);
        server.addBean(item);
        server.addBean(item2);

        server.commit();

        //Query all the docs that we have posted on solr
        SolrQuery query = new SolrQuery();
        query.setQuery( "*:*" );
        QueryResponse rsp = server.query( query );
        SolrDocumentList docs = rsp.getResults();
        System.out.println("All the docs that we have posted on solr:\n" + docs );

        //Query doc id=123456789 before updating it.
        query.setQuery( "123456789" );
        QueryResponse rsp1 = server.query( query );
        SolrDocumentList docs1 = rsp1.getResults();
        System.out.println("\nBefore updating doc id=123456789:\n" + docs1);

        //To update the document
        Map<String,Object> fieldModifier = new HashMap<>(1);
        fieldModifier.put("set", "HP pc with windows OS");
        document.setField("name", fieldModifier);  // add the map as the field value

        server.add(document);
        server.commit();

        Map<String,Object> fieldModifier1 = new HashMap<>(1);
        fieldModifier1.put("set", "800");
        document.setField("price", fieldModifier1);
        server.add( document );
        server.commit();
        sleep(10);

        //Query the doc id=123456789 after updating it
        query.setQuery( "123456789" );
        QueryResponse rsp2 = server.query( query );
        SolrDocumentList docs2 = rsp2.getResults();

        System.out.println("\nAfter updating doc id=123456789:\n" + docs2);


        //To delete a document from solr then query all the docs that are on solr.
        server.deleteById("George");

        server.commit();

        query.setQuery( "*:*" );
        QueryResponse rsp3 = server.query( query );
        SolrDocumentList docs3 = rsp3.getResults();
        System.out.println("\nAll the docs on solr after deleting id=George:\n" + docs3 );


        //delete all docs on solr and query the all docs on solr
        server.deleteByQuery("*:*");
        server.commit();

        QueryResponse rsp4 = server.query( query );
        SolrDocumentList docs4 = rsp4.getResults();
        System.out.println("\nsolr's database after deleting everything:\n" + docs4 );

    }
}