package com.sandbox.graphql.types;

import graphql.annotations.annotationTypes.GraphQLField;

public class RootMutation
{

    @GraphQLField
    public static VendorMutation vendor()
    {
        return new VendorMutation();
    }

}
