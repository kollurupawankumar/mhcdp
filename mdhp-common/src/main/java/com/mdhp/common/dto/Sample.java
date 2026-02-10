package com.mdhp.common.dto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Sample {

    //Input = [[A,Z], [P, D], [I,L]]
    public static void main(String[] args) {

        List<List<String>> input = Arrays.asList(
                Arrays.asList("A", "Z"), Arrays.asList("P", "D"), Arrays.asList("I", "L"), Arrays.asList("B", "S")
        );
        System.out.println("Input -- "+input);

        List<String> ip = input.stream().flatMap(Collection::stream).sorted().toList();

        List<List<String>> pairs = IntStream.range(0, ip.size())
                .boxed().collect(Collectors.groupingBy(index -> index/2, Collectors.mapping(ip::get, Collectors.toList())))
                .values().stream().toList();

        System.out.println("Output -"+pairs);

    }
}
