package com.sandbox.graphql.types;

import com.sandbox.graphql.types.input.UserInput;
import com.sandbox.graphql.types.output.User;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName("VENDOR_Mutation")
public class VendorMutation
{

    @GraphQLField
    public User createOrUpdateUser( final @GraphQLName("user") UserInput userInput )
    {
        User user = new User();

        user.setName( userInput.getName() );

        return user;
    }

}
