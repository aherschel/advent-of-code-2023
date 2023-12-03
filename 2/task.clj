(require '[clojure.string :as str])

(def test-input '("Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
                  "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"
                  "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"
                  "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"
                  "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"))
(def test-output 8)
(def bag-contents {:red 12 :green 13 :blue 14})

(def test-input-row "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue")
(def test-parsed-input {2 [{:blue 1 :green 2}
                           {:blue 4 :green 3 :red 1}
                           {:blue 1 :green 1}]})

(defn parse-row
  "Given a row as a string, return a game id mapped to the set of values"
  [row]
  {1 []})

;; Test Row Parsing Functionality
(comment
  (parse-row test-input-row)
  (= (parse-row test-input-row) test-parsed-input))

(defn
  process-row
  [val]
  (let [n (re-seq #"\d+" val)
        first (first (first n))
        last (last (last n))]
    (Integer/parseInt (str first last))))

(defn collect-sum
  "Collect the sum of a list of values"
  [vals] (reduce + vals))

(defn get-impossible-ids
  "Given a map of id to colored set, and a comparison value, identify games are not possible"
  [comparison-value games]
  ())

(defn process
  "Given a list of input strings, process and return the sum."
  [vals]
  (let [missing-ids '()]
    collect-sum missing-ids))

(defn read-file-as-list
  "Read the input file-path, and return as a seq"
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))] (doall (line-seq rdr))))

(comment
  (let [vals (read-file-as-list "2/input.txt")]
    (process vals))
  (let [processed-output (process test-input)]
    (= processed-output test-output)))
