package com.sandbox.graphql.types;

import graphql.annotations.annotationTypes.GraphQLField;

public class RootQuery
{

    @GraphQLField
    public static VendorQuery vendor()
    {
        return new VendorQuery();
    }

}
