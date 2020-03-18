package com.sandbox.graphql.types.output;

import java.util.Collections;
import java.util.List;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

import static com.sandbox.graphql.types.output.Account.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class Account
{

    public static final String TYPE_NAME = "VENDOR_Account";

    @GraphQLField
    public List<User> getUsers()
    {
        final User user = new User();
        user.setName( "Username" );

        return Collections.singletonList( user );
    }

}
