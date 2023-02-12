(ns clojure-swapi.rest-test
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:require [clj-http.client :as client]))

(defn get-sw-char-movies [sw-char-id]
  (let [sw-char (-> (str "https://swapi.dev/api/people/" sw-char-id)
                    client/get
                    :body
                    json/read-str)
        movies  (doall
                 (->> (get sw-char "films")
                      (map #(future (-> % client/get)))
                      (map deref)
                      (map (fn [res]
                             (->> res
                                  :body
                                  json/read-str)))
                      (map #(get % "title"))))]
    (str (get sw-char "name") " was featured in: " (string/join ", " movies) ".")))
(get-sw-char-movies 1)
;; => "Luke Skywalker was featured in: A New Hope, The Empire Strikes Back, Return of the Jedi, Revenge of the Sith."
(get-sw-char-movies 20)
;; => "Yoda was featured in: The Empire Strikes Back, Return of the Jedi, The Phantom Menace, Attack of the Clones, Revenge of the Sith."

