(require '[clojure.string :as str])

(def test-input '("32T3K 765"
                  "T55J5 684"
                  "KK677 28"
                  "KTJJT 220"
                  "QQQJA 483"))
(def test-output-part-1 6440)

(defn read-file-as-list
  "Read in the given file path and return a list of strings"
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))]
    (doall (line-seq rdr))))

(def input (read-file-as-list "7/input.txt"))

(defn exp [x n]
  (reduce * (repeat n x)))
(def place-multiplier 12)
(def hand-type-multipler (exp place-multiplier 6))

(def score-index
  (into {} (map-indexed #(vector %2 %1) '("2" "3" "4" "5" "6" "7" "8" "9" "T" "J" "Q" "K" "A"))))

(defn split-hand [hand] (str/split hand #""))
(defn compute-positional-card-score
  "Take the sum of card score-indexes + their place & the place-multipler"
  [hand part-score-index]
  (reduce + (map-indexed #(* (exp place-multiplier (inc %1)) (get part-score-index %2)) (reverse (split-hand hand)))))

(defn double-freq-into-hand-type-score
  [double-freq]
  (* (cond
       (= 1 (get double-freq 5)) 6
       (= 1 (get double-freq 4)) 5
       (and
        (= 1 (get double-freq 3))
        (= 1 (get double-freq 2))) 4
       (= 1 (get double-freq 3)) 3
       (= 2 (get double-freq 2)) 2
       (= 1 (get double-freq 2)) 1
       :else 0)
     hand-type-multipler))

(defn compute-hand-type-score
  "Given a hand compute the hand-type
   (5 of a kind, 4 of a kind, full house, three of a kind, two pair, one pair, none)
   [in that order] and multiply the hand type index by the hand-type-multiplier.
   If a wildcard is provided, then that value is added to any other double-freq"
  ([hand]
   (let [double-freq (frequencies (vals (frequencies (split-hand hand))))]
     (double-freq-into-hand-type-score double-freq)))
  ([hand wildcard]
   (let [freq                    (frequencies (split-hand hand))
         wildcard-count          (get freq wildcard 0)
         freqs-without-wildcard  (or (vals (dissoc freq wildcard)) '())
         max-val                 (apply max (if (empty? freqs-without-wildcard) '(0) freqs-without-wildcard))
         freq-vals-with-wildcard (conj (remove #(= % max-val) freqs-without-wildcard) (+ max-val wildcard-count))
         double-freq             (frequencies freq-vals-with-wildcard)]
     (double-freq-into-hand-type-score double-freq))))

(comment
  (if (empty? '()) '(1) '())
  (apply max '(1))
  (let [hand "JJJJJ" 
        wildcard "J"
        freq                    (frequencies (split-hand hand))
        wildcard-count          (get freq wildcard 0) 
        freqs-without-wildcard (or (vals (dissoc freq wildcard)) '())
        max-val (apply max (if (empty? freqs-without-wildcard) '(0) freqs-without-wildcard))]
    (conj (remove #(= % max-val) freqs-without-wildcard) (+ max-val wildcard-count))))

(defn compute-hand-score
  "Given a hand, compute a unique score to allow for a simple sort-by
   this will be done by first applying a top-level score for the hand type
   (where the value that this score type increments by is >> the per-card modifiers)
   then adding a modifier per-score in sequential order where each place is a multiple of 12"
  [hand]
  (+
   (compute-hand-type-score hand)
   (compute-positional-card-score hand score-index)))

(defn parse-hand
  "Given a hand as a string, parse into a map of :hand and :bid"
  [hand-str]
  (let [[hand bid] (str/split hand-str #" ")]
    {:hand hand :bid (Integer/parseInt bid)}))

(defn compute-bid-scores
  "Given a set of input rows, sort the hands by score,
   grab the bid scores (scaled by position)
   and sum them all up"
  [input-rows]
  (let [sorted-hands (sort-by #(compute-hand-score (:hand %)) (map parse-hand input-rows))
        bid-scores (map-indexed #(* (inc %1) (:bid %2)) sorted-hands)]
    (reduce + bid-scores)))

;; Tests for part 1
(comment
  (compute-bid-scores test-input)
  (= (compute-bid-scores test-input) test-output-part-1)
  (compute-bid-scores input))

;; Tests for part 2
(def test-output-part-2 5905)

;; J is now the lowest scoring individual card
(def score-index-part-2
  (into {} (map-indexed #(vector %2 %1) '("J" "2" "3" "4" "5" "6" "7" "8" "9" "T" "Q" "K" "A"))))

(def part-2-wildcard "J")

(defn compute-hand-score-part-2
  "Given a hand, compute a unique score to allow for a simple sort-by
   this will be done by first applying a top-level score for the hand type
   (where the value that this score type increments by is >> the per-card modifiers)
   then adding a modifier per-score in sequential order where each place is a multiple of 12"
  [hand]
  (+
   (compute-hand-type-score hand part-2-wildcard)
   (compute-positional-card-score hand score-index-part-2)))

(defn compute-bid-scores-part-2
  "Given a set of input rows, sort the hands by score,
   grab the bid scores (scaled by position)
   and sum them all up"
  [input-rows]
  (let [sorted-hands (sort-by #(compute-hand-score-part-2 (:hand %)) (map parse-hand input-rows))
        bid-scores (map-indexed #(* (inc %1) (:bid %2)) sorted-hands)]
    (reduce + bid-scores)))

(comment
  input
  (sort-by #(compute-hand-score-part-2 (:hand %)) (map parse-hand test-input))
  (compute-bid-scores-part-2 test-input)
  (= (compute-bid-scores-part-2 test-input) test-output-part-2)
  (compute-bid-scores-part-2 input))
