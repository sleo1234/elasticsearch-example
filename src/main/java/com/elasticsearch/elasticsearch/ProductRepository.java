package com.elasticsearch.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Repository
public class ProductRepository {

	
	  @Autowired
	    private ElasticsearchClient elasticsearchClient;
	    
	  private final String indexName = "products";
	  
	  public String createOrUpdateDocument(Product product) throws IOException {
		  
		  if (product.getId() == null) {
			  
			  
			  product.setId(RandomStringUtils.random(10, true, true));
		  }
		  IndexResponse response = elasticsearchClient.index(i-> i.index(indexName)
				                                                .id(product.getId())
				                                                .document(product));
				                                               
		  
		  
		  
		  
	        if(response.result().name().equals("Created")){
	            return new StringBuilder("Document has been successfully created.").toString();
	        }else if(response.result().name().equals("Updated")){
	            return new StringBuilder("Document has been successfully updated.").toString();
	        }
	        return new StringBuilder("Error while performing the operation.").toString();
	    

	 
		  
	  }
	  
	  
	  public Product getDocumentById (String id) throws ElasticsearchException, IOException {
		  Product product = null;
	        GetResponse<Product> response = elasticsearchClient.get(g -> g
	                        .index(indexName)
	                        .id(id),
	                Product.class
	        );

	        if (response.found()) {
	             product = response.source();
	            System.out.println("Product name " + product.getName());
	        } else {
	            System.out.println ("Product not found");
	        }

	       return product;
	  }
	 
	  public List<Product> getAllDocuments() throws IOException{
		  List<Product> products = new ArrayList<>();
		 
		  SearchRequest searchRequest =  SearchRequest.of(s -> s.index(indexName));
	        SearchResponse searchResponse =  elasticsearchClient.search(searchRequest, Product.class);
	        List<Hit> hits = searchResponse.hits().hits();
	        
	       for ( Hit object : hits) {
	    	   products.add((Product) object.source());
	       }
		  return products;
	  }
	  
}
