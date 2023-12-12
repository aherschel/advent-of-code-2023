(require '[clojure.string :as str])

(defn read-file-as-list
  "Read in the given file path and return a list of strings"
  [file-path]
  (with-open [rdr (java.io.BufferedReader. (java.io.FileReader. file-path))]
    (doall (line-seq rdr))))

(defn split-string
  "Split a string into a list of chars"
  [hand]
  (str/split hand #""))

(defn directions-to-keyword-list
  "Given a string of characters, convert to a list of keywords"
  [direction-str]
  (map #(keyword %) (split-string direction-str)))

(defn string-to-direction
  "Convert a string representing a node and set of directional next nodes to a map"
  [row]
  (let [[node dirs] (str/split row #" = ")
        [left right] (str/split (str/replace dirs #"\(|\)" "") #", ")]
    {:node node :dirs {:L left :R right}}))

(defn string-list-to-map
  "Convert a list of strings into a list of maps"
  [rows]
  (into {} (map (comp #(vector (:node %) (:dirs %)) string-to-direction) rows)))

(def test-directions (directions-to-keyword-list "LR"))
(def test-nodes (string-list-to-map '("11A = (11B, XXX)"
                                      "11B = (XXX, 11Z)"
                                      "11Z = (11B, XXX)"
                                      "22A = (22B, XXX)"
                                      "22B = (22C, 22C)"
                                      "22C = (22Z, 22Z)"
                                      "22Z = (22B, 22B)"
                                      "XXX = (XXX, XXX)")))
(def test-step-count 6)
(def real-directions (directions-to-keyword-list "LRRRLRRRLRRLRLRRLLRRLLRLRRRLRRLRRRLRRLLRLRLRRRLRLLRRRLLRLRRRLRLRRRLRRRLRRRLRRRLRLLLRRRLRRLRRLRRRLRLRLRRLRLRRRLRLRLRLRRRLRRLRLRRRLRRLRRRLRRLLRRRLLRLLRLRRRLRLLRRLLRRRLRLLRRLRLRRLRRRLRLRLRLLRLRRRLRRRLRLLLRRRLRLRRRLRRLRRLLLLRLRRRLRLRRRLLRRRLRRRLRRRLLLRLRLRLLLLRRRLRRLRRRLRLRLRLRRRLRLRRRR"))
(def real-nodes (string-list-to-map (read-file-as-list "8/input.txt")))
(def starting-point "AAA")
(def endpoint-point "ZZZ")

;; Flip between test and real input here
(def nodes real-nodes)
(def directions real-directions)


(defn get-next-node
  "Given a node and direction, get the next node"
  [node direction]
  (get-in nodes [node direction]))

(def state (atom {:node starting-point
                  :steps 0}))

(defn compute-next-state
  "Compute the next state atom"
  [{:keys [node steps]}]
  {:node (get-next-node node (nth directions (mod steps (count directions))))
   :steps (inc steps)})

(while (not (= (:node @state) endpoint-point))
  (swap! state compute-next-state))

(println (str "Got to " endpoint-point " in " (:steps @state) " steps"))

;; Part 2
(defn get-strings-ending-with
  "Given a list of strings, return the strings that end with a given char"
  [vals suffix]
  (map #(apply str %) (filter #(= suffix (last %)) (map split-string vals))))

(def part-2-state (atom {:nodes (get-strings-ending-with (keys nodes) "A")
                         :steps 0}))

(defn compute-next-part-2-state
  "Compute the next state atom"
  [{:keys [nodes steps]}]
  (let [direction (nth directions (mod steps (count directions)))]
    {:nodes (map #(get-next-node % direction) nodes)
     :steps (inc steps)}))


(defn get-steps-for-node
  "For a given node, compute and return the step count"
  [node]
  (loop [curr-node node
         step 0]
    (if (= (last (split-string curr-node)) "Z")
      step
      (recur (get-next-node curr-node (nth directions (mod step (count directions)))) (inc step)))))

(def start-nodes (get-strings-ending-with (keys nodes) "A"))
(def cycle-lengths (map get-steps-for-node start-nodes))


;; Copied from clojure.math.numeric-tower

(defn gcd "(gcd a b) returns the greatest common divisor of a and b" [a b]
  (if (or (not (integer? a)) (not (integer? b)))
    (throw (IllegalArgumentException. "gcd requires two integers"))
    (loop [a (abs a) b (abs b)]
      (if (zero? b) a,
          (recur b (mod a b))))))

(defn lcm
  "(lcm a b) returns the least common multiple of a and b"
  [a b]
  (when (or (not (integer? a)) (not (integer? b)))
    (throw (IllegalArgumentException. "lcm requires two integers")))
  (cond (zero? a) 0
        (zero? b) 0
        :else (abs (* b (quot a (gcd a b))))))

(reduce lcm cycle-lengths)
