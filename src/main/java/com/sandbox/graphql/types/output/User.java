package com.sandbox.graphql.types.output;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

import static com.sandbox.graphql.types.output.User.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class User
{

    public static final String TYPE_NAME = "VENDOR_User";

    @GraphQLField
    @GraphQLName("name")
    private String name;

    public User()
    {
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
