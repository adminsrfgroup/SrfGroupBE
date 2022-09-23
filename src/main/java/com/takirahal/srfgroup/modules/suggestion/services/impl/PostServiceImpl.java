package com.takirahal.srfgroup.modules.suggestion.services.impl;

import com.takirahal.srfgroup.modules.offer.entities.Offer;
import com.takirahal.srfgroup.modules.suggestion.entities.Post;
import com.takirahal.srfgroup.modules.suggestion.repositories.PostRepository;
import com.takirahal.srfgroup.modules.suggestion.services.PostService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PostServiceImpl implements PostService {

    private static final String PRODUCT_INDEX = "postindex";

    @Autowired
    ElasticsearchOperations elasticsearchOperations;


    @Autowired
    PostRepository postRepository;

    @Autowired
    RestHighLevelClient client;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public List<Post> fetchSuggestions(Post post, Pageable pageable) {

        List<Post> suggestions = new ArrayList<Post>();

        try {

            /*
            // Suggestion 0
            String input = "";
            SearchSourceBuilder searchSourceBuilder0 = new SearchSourceBuilder();
            SuggestionBuilder termSuggestionBuilder0 = SuggestBuilders.completionSuggestion("my_index_suggest").text(input);

            SuggestBuilder suggestBuilder0 = new SuggestBuilder();
            suggestBuilder0.addSuggestion("suggest_field", termSuggestionBuilder0);
            searchSourceBuilder0.suggest(suggestBuilder0);

            SearchRequest searchRequest0 = new SearchRequest();
            searchRequest0.source(searchSourceBuilder0);

            SearchResponse searchResponse = client.search(searchRequest0, RequestOptions.DEFAULT);
            List<String> result0 = StreamSupport.stream(Spliterators.spliteratorUnknownSize(searchResponse.getSuggest().iterator(), Spliterator.ORDERED), false)
                    .flatMap(suggestion -> suggestion.getEntries().get(0).getOptions().stream())
                    .map((Suggest.Suggestion.Entry.Option option) -> option.getText().toString())
                    .collect(Collectors.toList());


            // Suggestion 1
            QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "*"+post.getName()+"*")
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(2)
                    .maxExpansions(10);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(matchQueryBuilder);
            sourceBuilder.from(0);
            sourceBuilder.size(5);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse1 = client.search(searchRequest,RequestOptions.DEFAULT);
            SearchHit[] searchHits1 = searchResponse1.getHits().getHits();

            //Printing the results
            for(SearchHit theHit: searchHits1) {
                System.out.println("1-----------------------------------------------------");
                System.out.println(theHit);
                System.out.println("1-----------------------------------------------------");
            }
            */


            // Suggestion 2
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(5);
            SuggestionBuilder termSuggestionBuilder =
                    SuggestBuilders.termSuggestion("name").text("name");

            SuggestBuilder suggestBuilder = new SuggestBuilder();
            suggestBuilder.addSuggestion("suggest_post", termSuggestionBuilder);
            searchSourceBuilder.suggest(suggestBuilder);

            SearchRequest request = new SearchRequest();
            request.source(searchSourceBuilder);

            SearchResponse suggestResponse2 = client.search(request, RequestOptions.DEFAULT);

            SearchHit[] searchHits2 = suggestResponse2.getHits().getHits();

            //Printing the results
            for(SearchHit theHit: searchHits2) {
                System.out.println("2-----------------------------------------------------");
                System.out.println(theHit);
                System.out.println("2-----------------------------------------------------");
            }


            // Suggestion 3
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            BoolQueryBuilder bq = QueryBuilders.boolQuery() ;

            if( post.getName()!=null && !post.getName().equals("") ){
                bq.must(QueryBuilders.matchPhraseQuery("name", "*"+post.getName()+"*")) ;
            }

            if( post.getDescription()!=null && !post.getDescription().equals("") ){
                bq.must(QueryBuilders.wildcardQuery("description", "*"+post.getDescription()+"*")) ;
            }

            boolQueryBuilder.must(bq);

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .withPageable(pageable)
                    .build();

            SearchHits<Post> searchSuggestions =
                    elasticsearchOperations.search(searchQuery,
                            Post.class,
                            IndexCoordinates.of(PRODUCT_INDEX));


            searchSuggestions.getSearchHits().forEach(searchHit->{
                suggestions.add(searchHit.getContent());
            });

            return suggestions;
        }
        catch (Exception ex){
            return suggestions;
        }
    }

    @Override
    public List<Post> searchPosts(Post post, Pageable pageable) {

        List<Post> suggestions = new ArrayList<Post>();

        try {


            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            BoolQueryBuilder bq = QueryBuilders.boolQuery() ;

            if( post.getName()!=null && !post.getName().equals("") ){
                bq.must(QueryBuilders.matchPhraseQuery("name", "*"+post.getName()+"*")) ;
            }

            if( post.getDescription()!=null && !post.getDescription().equals("") ){
                bq.must(QueryBuilders.matchPhraseQuery("description", "*"+post.getDescription()+"*")) ;
            }

            boolQueryBuilder.must(bq);

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .withPageable(pageable)
                    .build();

            SearchHits<Post> searchSuggestions =
                    elasticsearchOperations.search(searchQuery,
                            Post.class,
                            IndexCoordinates.of(PRODUCT_INDEX));


            searchSuggestions.getSearchHits().forEach(searchHit->{
                suggestions.add(searchHit.getContent());
            });

            return suggestions;
        }
        catch (Exception ex){
            return suggestions;
        }
    }


    private void _addSettings() {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.suggestions.type", "custom")
                .put("index.analysis.analyzer.suggestions.tokenizer", "standard")
                .build();
    }


    private void _addAnalyzer() {
        AnalyzeRequest request = AnalyzeRequest.buildCustomAnalyzer("standard")
                .addCharFilter("html_strip")
                .addTokenFilter("lowercase")
                // .addTokenFilter(stopFilter)
                .build("<b>Some text to analyze</b>");
    }


    private void _addMapping() throws IOException {
        /*
        PutMappingRequest requestMapping = new PutMappingRequest("seminar_map");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("seminar_nummer");
                {
                    builder.field("analyzer", "case_insensitive_analyzer");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        requestMapping.source(builder);
        client.indices().putMapping(requestMapping, RequestOptions.DEFAULT);
        */
    }

}
