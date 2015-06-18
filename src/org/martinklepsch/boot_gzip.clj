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

(defn ^:private origin-target-from-regex [regexes fileset]
  (into {}
        (for [f (c/by-re regexes (c/input-files fileset))]
          {(:path f) (str (:path f) ".gz")})))

(c/deftask gzip
  "Generic Boot task to compress files using Gzip.

   All files matching the regular expression provided
   will be gzipped to their original location with .gz appended

   Also you can supply arbitrary {origin target} mappings
   using the `files` option.

   If a file is matched by the regex and the files option the
   latter takes precedence."
  [r regex REGEX         [regex]   "Gzip all matching files to their respective location and append .gz"
   f files ORIGIN:TARGET {str str} "{origin target} map of files to gzip"]
  (let  [tmp (c/tmp-dir!)]
    (c/with-pre-wrap fileset
      (let [from-regex  (origin-target-from-regex regex fileset)
            to-compress (merge from-regex files)]
        (u/dbug "Gzip mapping: %s\n"(pr-str to-compress))
        (doseq [[origin target] to-compress]
          (let [in  (file-by-path origin fileset)
                out (io/file tmp target)]
            (io/make-parents out)
            (gzip-file in out)
            (let [origin-size (.length in)
                  new-size    (.length out)]
              (u/info "Gzipped %s (%s) to %s (%s), saving %s%%\n"
                      origin (bytes->human-readable origin-size)
                      target (bytes->human-readable new-size)
                      (percent-saved origin-size new-size))))))
      (-> fileset (c/add-resource tmp) c/commit!))))
