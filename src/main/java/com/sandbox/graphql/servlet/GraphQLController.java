/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sandbox.graphql.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandbox.graphql.schema.GraphQLProvider;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.introspection.IntrospectionQuery;

@Controller
@RequestMapping(value = "/graphql")
public class GraphQLController
{

    private final GraphQLProvider graphQLProvider;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GraphQLController( final GraphQLProvider schemaProvider )
    {
        this.graphQLProvider = schemaProvider;
    }

    @GetMapping
    public void doGet( HttpServletRequest req, HttpServletResponse resp )
        throws IOException
    {
        String query = req.getParameter( "query" );
        if ( "/schema.json".equals( req.getPathInfo() ) )
        {
            query = IntrospectionQuery.INTROSPECTION_QUERY;
        }
        String operationName = req.getParameter( "operationName" );
        String variableStr = req.getParameter( "variables" );
        Map<String, Object> variables = new HashMap<>();
        if ( ( variableStr != null ) && ( variableStr.trim().length() > 0 ) )
        {
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>()
            {
            };
            variables = objectMapper.readValue( variableStr, typeRef );
        }

        setupCORSHeaders( req, resp );
        executeGraphQLRequest( resp, query, operationName, variables );
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    public void doPost( HttpServletRequest req, HttpServletResponse resp )
        throws IOException
    {
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>()
        {
        };
        Map<String, Object> body = objectMapper.readValue( req.getInputStream(), typeRef );

        String query = (String) body.get( "query" );
        String operationName = (String) body.get( "operationName" );
        Map<String, Object> variables = (Map<String, Object>) body.get( "variables" );

        if ( variables == null )
        {
            variables = new HashMap<>();
        }

        setupCORSHeaders( req, resp );
        executeGraphQLRequest( resp, query, operationName, variables );
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public void doOptions( HttpServletRequest req, HttpServletResponse resp )
        throws IOException
    {
        setupCORSHeaders( req, resp );
        resp.flushBuffer();
    }

    private void executeGraphQLRequest( HttpServletResponse resp, String query, String operationName, Map<String, Object> variables )
        throws IOException
    {
        if ( query == null || query.trim().length() == 0 )
        {
            throw new IllegalArgumentException( "Query cannot be empty or null" );
        }

        final ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query( query )
            .variables( variables )
            .operationName( operationName )
            .build();

        final ExecutionResult executionResult = graphQLProvider.getGraphQL().execute( executionInput );

        final Map<String, Object> specificationResult = executionResult.toSpecification();

        objectMapper.writeValue( resp.getWriter(), specificationResult );
    }

    private void setupCORSHeaders( HttpServletRequest httpServletRequest, ServletResponse response )
    {
        if ( !( response instanceof HttpServletResponse ) )
        {
            return;
        }

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        httpServletResponse.setHeader( "Access-Control-Allow-Origin", getOriginHeaderFromRequest( httpServletRequest ) );
        httpServletResponse.setHeader( "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-Apollo-Tracing" );
        httpServletResponse.setHeader( "Access-Control-Allow-Credentials", "true" );
        httpServletResponse.setHeader( "Access-Control-Allow-Methods", "OPTIONS, POST, GET" );
    }

    private String getOriginHeaderFromRequest( final HttpServletRequest httpServletRequest )
    {
        return httpServletRequest != null && httpServletRequest.getHeader( "Origin" ) != null
            ? httpServletRequest.getHeader( "Origin" )
            : "*";
    }

}
