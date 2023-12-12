(ns aoc.tests
  (:require [aoc.utils :as utils]
            [clojure.test :refer :all]
            [aoc.day9 :as day9]))

(def day9-input (utils/read-file-as-list "resources/day9_test.txt"))
(deftest day9
  (testing "Part 1" (is (= (day9/part1 day9-input) 114)))
  (testing "Part 2" (is (= (day9/part2 day9-input) 2))))

;; Manually run tests
(comment
  (run-all-tests)
  (run-test day9))
