(ns org.martinklepsch.boot-gzip
  {:boot/export-tasks true}
  (:require [boot.core       :as c]
            [boot.util       :as u]
            [clojure.java.io :as io])
  (:import java.util.zip.GZIPInputStream
           java.util.zip.GZIPOutputStream))

(defn ^:private bytes->human-readable [bytes & [si?]]
  (let [unit (if si? 1000 1024)]
    (if (< bytes unit) (str bytes " B")
         (let [exp (int  (/ (java.lang.Math/log bytes)
                            (java.lang.Math/log unit)))
               pre (str (nth (if si? "kMGTPE" "KMGTPE") (dec exp)) (if-not si? "i" ))]
           (format "%.1f %sB" (/ bytes (Math/pow unit exp)) pre)))))

(defn ^:private percent-saved [old new]
  (-> (* 100 (/ (- old new) old))
      float
      java.lang.Math/round))

(defn ^:private file-by-path [path fileset]
  (c/tmp-file (get (:tree fileset) path)))

(defn ^:private gzip-file
  "Writes the contents of input to output, compressed.

  input: something which can be copied from by io/copy.
  output: something which can be opend by io/output-stream.
      The bytes written to the resulting stream will be gzip compressed."
  [input output & opts]
  (with-open [output (-> output io/output-stream GZIPOutputStream.)]
    (apply io/copy input output opts)))

(c/deftask gzip
  [f files ORIGIN:TARGET {str str} "{origin target} map of files to gzip"]
  (let  [tmp (c/tmp-dir!)]
    (c/with-pre-wrap fileset
      (doseq [[origin target] files]
        (let [in  (file-by-path origin fileset)
              out (io/file tmp target)]
          (io/make-parents out)
          (gzip-file in out)
          (let [origin-size (.length in)
                new-size    (.length out)]
            (u/info "Gzipped %s (%s) to %s (%s), saving %s%%\n"
                    origin (bytes->human-readable origin-size)
                    target (bytes->human-readable new-size)
                    (percent-saved origin-size new-size)))))
      (-> fileset (c/add-resource tmp) c/commit!))))
