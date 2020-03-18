package com.sandbox.graphql.schema;

import org.springframework.stereotype.Component;

import com.sandbox.graphql.types.RootMutation;
import com.sandbox.graphql.types.RootQuery;
import com.sandbox.graphql.types.output.Account;
import com.sandbox.graphql.types.output.User;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.annotations.AnnotationsSchemaCreator;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLTypeVisitorStub;
import graphql.schema.SchemaTransformer;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;

import static graphql.util.TreeTransformerUtil.changeNode;

@Component
public class SchemaProviderImpl
    implements GraphQLProvider
{

    @Override
    public GraphQL getGraphQL()
    {
        final GraphQLSchema originalSchema = getSchema();

        return GraphQL.newGraphQL( transformAndGetSchema( originalSchema ) ).build();
    }

    private GraphQLSchema transformAndGetSchema( final GraphQLSchema originalSchema )
    {
        final SchemaTransformer schemaTransformer = new SchemaTransformer();

        return schemaTransformer.transform( originalSchema, new GraphQLTypeVisitorStub()
        {

            public TraversalControl visitGraphQLObjectType( GraphQLObjectType node, TraverserContext<GraphQLSchemaElement> context )
            {
                final GraphQLCodeRegistry.Builder registryBuilder = context.getVarFromParents( GraphQLCodeRegistry.Builder.class );

                if ( Account.TYPE_NAME.equals( node.getName() ) )
                {
                    final GraphQLObjectType transformedNode = node.transform( builder -> builder.field(
                        GraphQLFieldDefinition.newFieldDefinition().name( "dynamicField" ).type( Scalars.GraphQLString ).build() ) );

                    registryBuilder.dataFetcher( FieldCoordinates.coordinates( Account.TYPE_NAME, "dynamicField" ),
                                                 (DataFetcher<String>) environment -> "I'm a dynamic filed of " + Account.TYPE_NAME );

                    return changeNode( context, transformedNode );
                }

                if ( User.TYPE_NAME.equals( node.getName() ) )
                {
                    final GraphQLObjectType transformedNode = node.transform( builder -> builder.field(
                        GraphQLFieldDefinition.newFieldDefinition().name( "dynamicField" ).type( Scalars.GraphQLString ).build() ) );

                    registryBuilder.dataFetcher( FieldCoordinates.coordinates( User.TYPE_NAME, "dynamicField" ),
                                                 (DataFetcher<String>) environment -> "I'm a dynamic filed of " + User.TYPE_NAME );

                    return changeNode( context, transformedNode );
                }

                return visitGraphQLType( node, context );
            }
        } );
    }

    private GraphQLSchema getSchema()
    {
        final GraphQLAnnotations graphQLAnnotations = new GraphQLAnnotations();

        graphQLAnnotations.getContainer().setInputPrefix( "" );
        graphQLAnnotations.getContainer().setInputSuffix( "Input" );

        return AnnotationsSchemaCreator.newAnnotationsSchema()
            .query( RootQuery.class )
            .mutation( RootMutation.class )
            .setAnnotationsProcessor( graphQLAnnotations )
            .build();
    }


}
