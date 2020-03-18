package com.sandbox.graphql.types;

import com.sandbox.graphql.types.output.Account;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName("VENDOR_Query")
public class VendorQuery
{

    @GraphQLField
    public Account getAccount()
    {
        return new Account();
    }

}
