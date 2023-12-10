(require '[clojure.string :as str])

(defn read-file-as-list
  "Read the input file-path, and return as a seq"
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))] (doall (line-seq rdr))))

(def test-input '("Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
                  "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"
                  "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"
                  "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"
                  "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"))
(def test-output 8)
(def bag-contents {:red 12 :green 13 :blue 14})

(def test-input-row "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue")
(def test-parsed-input {:num    2
                        :rounds '({:blue 1 :green 2}
                                  {:blue 4 :green 3 :red 1}
                                  {:blue 1 :green 1})})

(defn parse-row
  "Given a row as a string, return a map with a game number and rounds array"
  [row]
  (let [[game-name rounds-str] (str/split row #":")]
    {:num    (Integer/parseInt (re-find #"\d+" game-name))
     :rounds (map (fn [round]
                    (apply merge (map (fn [round-part]
                                        (let [[num color] (str/split (str/trim round-part) #" ")]
                                          {(keyword color) (Integer/parseInt num)}))
                                      (str/split (str/trim round) #","))))
                  (str/split rounds-str #";"))}))

;; Test Row Parsing Functionality
(comment
  (parse-row test-input-row)
  (= (parse-row test-input-row) test-parsed-input))

(defn row-passes
  "Return a boolean determining a row passes the input bag contents"
  [{ :keys [rounds]}]
  (every?
   (fn [round]
     (every?
      (fn [[color num]] (<= num (color bag-contents)))
      (seq round)))
   rounds))

;; Test row-passes
(comment
  (row-passes test-parsed-input)
  (= (row-passes test-parsed-input) true))

(defn process-rows-part-1
  "Given a list of rows as strings, parse
   then filter to those which meet the pass criteria,
   and finally grab the sum of game numbers"
  [rows]
  (reduce + (map :num (filter row-passes (map parse-row rows)))))

(def input (read-file-as-list "2/input.txt"))

;; Part 1 tests and results
(comment 
  (process-rows-part-1 test-input)
  (= test-output (process-rows-part-1 test-input))
  (process-rows-part-1 input))

(println (str "Part 1 Answer:" (process-rows-part-1 input)))

(def colors '(:blue :red :green))

(defn get-row-power
  "Get the max values of each keyword across all rounds, and return them multiplied together"
  [{:keys [rounds]}]
  (apply * (map #(apply max (filter (comp not nil?) (map % rounds))) colors)))

(defn process-rows-part-2
  "Given a list of rows as strings, parse
   then get the max value of each keyword,
   multiple those together and sum them up"
  [rows]
  (reduce + (map (comp get-row-power parse-row) rows)))

;; Part 2 tests and results
(comment
  (get-row-power test-parsed-input)
  (process-rows-part-2 test-input)
  (= (process-rows-part-2 test-input) 2286)
  (process-rows-part-2 input))

(println (str "Part 2 Answer:" (process-rows-part-2 input)))
