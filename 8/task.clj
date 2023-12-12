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

(while (not (=
             (count (:nodes @part-2-state))
             (count (get-strings-ending-with (:nodes @part-2-state) "Z"))))
  (if (= (mod (:steps @part-2-state) 100000) 0)
    (println (str "Step " (:steps @part-2-state) " and state " @part-2-state)))
  (swap! part-2-state compute-next-part-2-state))

(println (str "Got to " endpoint-point " in " (:steps @part-2-state) " steps"))
