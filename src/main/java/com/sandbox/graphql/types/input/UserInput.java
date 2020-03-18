package com.sandbox.graphql.types.input;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName("VENDOR_User")
public class UserInput
{

    @GraphQLField
    @GraphQLName("name")
    private String name;

    public UserInput( final @GraphQLName("name") String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

}
