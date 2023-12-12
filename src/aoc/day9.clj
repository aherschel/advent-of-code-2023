(ns aoc.day9
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [aoc.utils :as utils]))

(def test-input (utils/read-file-as-list "resources/day9_test.txt"))
(def real-input (utils/read-file-as-list "resources/day9.txt"))

(defn to-ints
  "Given a string representing a row of ints
   separated by a space, return a list of ints"
  [row]
  (map #(Integer/parseInt %) (str/split row #" ")))

(defn diff-vals
  "Given a list of values, return a sliding subtractions across each pair"
  [vals]
  (reverse (loop [curr vals
                  sums '()]
             (if (> (count curr) 1)
               (recur (rest curr) (conj sums (- (second curr) (first curr))))
               sums))))

(defn all-0s?
  "Return true if array is all values"
  [vals]
  (every? #(= 0 %) vals))

(defn compute-next
  "Compute the next value in a sequence"
  [vals]
  (if (all-0s? vals)
    0
    (+ (last vals) (compute-next (diff-vals vals)))))

(defn part1
  "Solve problem part 1"
  [input]
  (reduce + (map (comp compute-next to-ints) input)))

;; Part 1 Tests
(comment
  (part1 test-input)
  (= (part1 test-input) 114)
  (part1 real-input))

(defn compute-previous
  "Compute the previous value in a sequence"
  [vals]
  (if (all-0s? vals)
    0
    (- (first vals) (compute-previous (diff-vals vals)))))

(defn part2
  "Solve problem part 2"
  [input]
  (reduce + (map (comp compute-previous to-ints) input)))

;; Part 2 Tests
(comment
  (part2 test-input)
  (= (part2 test-input) 2)
  (part2 real-input))
