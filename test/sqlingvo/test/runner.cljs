(ns sqlingvo.test.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [sqlingvo.compiler-test]
            [sqlingvo.copy-test]
            [sqlingvo.core-test]
            [sqlingvo.create-table-test]
            [sqlingvo.db-test]
            [sqlingvo.delete-test]
            [sqlingvo.drop-table-test]
            [sqlingvo.explain-test]
            [sqlingvo.expr-test]
            [sqlingvo.insert-test]
            [sqlingvo.materialized-view-test]
            [sqlingvo.select-test]
            [sqlingvo.truncate-test]
            [sqlingvo.update-test]
            [sqlingvo.util-test]
            [sqlingvo.values-test]
            [sqlingvo.with-test]))

(doo-tests
 'sqlingvo.compiler-test
 'sqlingvo.copy-test
 'sqlingvo.core-test
 'sqlingvo.create-table-test
 'sqlingvo.db-test
 'sqlingvo.delete-test
 'sqlingvo.drop-table-test
 'sqlingvo.explain-test
 'sqlingvo.expr-test
 'sqlingvo.insert-test
 'sqlingvo.materialized-view-test
 'sqlingvo.select-test
 'sqlingvo.truncate-test
 'sqlingvo.update-test
 'sqlingvo.util-test
 'sqlingvo.values-test
 'sqlingvo.with-test)
