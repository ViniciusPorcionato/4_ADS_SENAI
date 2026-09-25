package com.example.imagemPecas.infra.repository.specs;

public class GenericSpecs {
    private GenericSpecs(){}

    public static <T> Specification<T> conjunction(){
        return (root, q, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
