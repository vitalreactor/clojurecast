(ns clojurecast.lang.util
  (:require [taoensso.nippy :as nippy]))

(defn- nippy-opts
  []
  (cond-> {}
    (System/getProperty "clojurecast.key")
    (assoc :password (System/getProperty "clojurecast.key"))))

(defn freeze
  [x]
  (nippy/freeze x (nippy-opts)))

(defn thaw
  [x]
  (nippy/thaw x (nippy-opts)))
