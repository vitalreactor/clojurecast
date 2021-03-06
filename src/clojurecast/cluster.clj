;; Copyright (c) Vital Labs, Inc. All rights reserved.  The use and
;; distribution terms for this software are covered by the MIT
;; License (https://opensource.org/licenses/MIT) which can be found
;; in the file LICENSE at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be
;; bound by the terms of this license.  You must not remove this notice,
;; or any other, from this software.

(ns clojurecast.cluster
  (:require [clojurecast.core :as cc]
            [com.stuartsierra.component :as com])
  (:import [com.hazelcast.core Cluster]))

(defn current-time-millis
  (^long [] (.getClusterTime (cc/cluster)))
  (^long [instance] (.getClusterTime (cc/cluster instance))))

(defn ^com.hazelcast.core.Member local-member
  ([]
   (.getLocalMember (cc/cluster)))
  ([instance]
   (.getLocalMember (cc/cluster instance))))

(defn ^java.util.Set members
  ([]
   (.getMembers (cc/cluster)))
  ([instance]
   (.getMembers (cc/cluster instance))))

(defn ^String local-member-uuid
  ([]
   (.getUuid (local-member)))
  ([instance]
   (.getUuid (local-member instance))))

(defn ^com.hazelcast.core.IMap membership-listeners
  ([]
   (cc/distributed-map "cluster/membership-listeners"))
  ([instance]
   (cc/distributed-map instance "cluster/membership-listeners")))

(defn add-membership-listener
  [listener & {:keys [id]}]
  (when-not (and id (.containsKey (membership-listeners) id))
    (let [registration-id (.addMembershipListener (cc/cluster) listener)]
      (when id
        (.put (membership-listeners) id registration-id))
      registration-id)))

(defn remove-membership-listener
  [^String id]
  (if-let [registration-id (.get (membership-listeners) id)]
    (do
      (.remove (membership-listeners) id)
      (.removeMembershipListener (cc/cluster) registration-id))
    (.removeMembershipListener (cc/cluster) id)))

(defn is-master?
  ([]
   (identical? (first (members)) (local-member)))
  ([instance]
   (identical? (first (members instance)) (local-member instance))))
