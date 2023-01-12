package com.elasticsearch.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

@Repository
public class ProductRepository {

	@Autowired
	private ElasticsearchClient elasticsearchClient;
 
	@Autowired
	GenericRepository repo = new GenericRepository();
	private final String indexName = "products";

	

	public String createOrUpdateDocument(Product product) throws IOException {

		if (product.getId() == null) {

			product.setId(RandomStringUtils.random(10, true, true));
		}
		IndexResponse response = elasticsearchClient
				.index(i -> i.index(indexName).id(product.getId()).document(product));

		if (response.result().name().equals("Created")) {
			return new StringBuilder("Document has been successfully created.").toString();
		} else if (response.result().name().equals("Updated")) {
			return new StringBuilder("Document has been successfully updated.").toString();
		}
		return new StringBuilder("Error while performing the operation.").toString();

	}

	public Product getDocumentById(String id) throws ElasticsearchException, IOException {
		Product product = null;
		GetResponse<Product> response = elasticsearchClient.get(g -> g.index(indexName).id(id), Product.class);

		if (response.found()) {
			product = response.source();
			System.out.println("Product name " + product.getName());
		} else {
			System.out.println("Product not found");
		}

		return product;
	}

	public List<Product> getAllDocuments() throws IOException {
		List<Product> products = new ArrayList<>();

		SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
		SearchResponse searchResponse = elasticsearchClient.search(searchRequest, Product.class);
		List<Hit> hits = searchResponse.hits().hits();

		for (Hit object : hits) {
			products.add((Product) object.source());
		}
		return products;
	}
            
	public List<Product> getProductThatContains(String searchText) throws ElasticsearchException, IOException{
		System.out.println("-----------" + searchText);
		List<Product> products = repo.getByFieldName("name", indexName, searchText);
		
		return products;
	}

	
	public List<Product> getProductFromPriceRange(double minPrice, double maxPrice) throws ElasticsearchException, IOException{
		List<Product> products = new ArrayList<>();
		Query byMaxPrice = RangeQuery.of  (m -> m.field("price")
				                                     .lte(JsonData.of(maxPrice)))._toQuery();
				
		Query byMinPrice = RangeQuery.of  (m -> m.field("price")
                .gte(JsonData.of(minPrice)))._toQuery();
                                       
		
		SearchResponse searchResponse = elasticsearchClient.search(s -> s.index(indexName)
				                                                             .query(q -> 
				                                                             q.bool(b -> 
				                                                             b.must(byMinPrice).
				                                                              must(byMaxPrice)))
				                                                            , Product.class);
		List<Hit<Product>> hitsResponse = searchResponse.hits().hits();
		 List<Hit<Product>> hits = hitsResponse;
	    
	    products = convertTo(hits);
	    return products;
	}
	
	public <T> List<T> convertTo (List<Hit<T>> hitsList){
		
		
		 List<T> returnedList = new ArrayList<>();
		for (Hit<T> hit : hitsList) {
			returnedList.add(hit.source());
		}
		return returnedList;
	}

}