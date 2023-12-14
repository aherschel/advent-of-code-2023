(ns aoc.tests
  (:require [aoc.utils :as utils]
            [clojure.test :refer :all]
            [aoc.day9 :as day9]
            [aoc.day10 :as day10]))

(deftest day9
  (let [test-in (utils/read-file-as-list "resources/day9_test.txt")
        part-1-expected 114
        part-2-expected 2]
    (testing "Part 1" (is (= (day9/part1 test-in) part-1-expected)))
    (testing "Part 2" (is (= (day9/part2 test-in) part-2-expected)))))

(deftest day10
  (let [test-in-1 (utils/read-file-as-list "resources/day10_test.txt")
        test-in-2 (utils/read-file-as-list "resources/day10_test2.txt")
        part-1-expected 8
        part-2-expected 0] 
    (testing "Part 1 Input 1" (is (= (day10/part1 test-in-1) part-1-expected))) 
    (testing "Part 1 Input 2" (is (= (day10/part1 test-in-2) part-1-expected)))
    (testing "Part 2 Input 1" (is (= (day10/part2 test-in-1) part-2-expected)))
    (testing "Part 2 Input 2" (is (= (day10/part2 test-in-2) part-2-expected)))))

;; Manually run tests
(comment
  (run-all-tests)
  (run-test day9)
  (run-test day10))
