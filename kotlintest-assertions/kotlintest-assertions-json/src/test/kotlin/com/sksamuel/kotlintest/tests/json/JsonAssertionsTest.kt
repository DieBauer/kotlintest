package com.sksamuel.kotlintest.tests.json

import io.kotlintest.assertions.json.shouldContainJsonKey
import io.kotlintest.assertions.json.shouldContainJsonKeyValue
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.assertions.json.shouldNotContainJsonKey
import io.kotlintest.assertions.json.shouldNotContainJsonKeyValue
import io.kotlintest.assertions.json.shouldNotMatchJson
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

const val json = """{
    "store": {
        "book": [
            {
                "category": "reference",
                "author": "Nigel Rees",
                "title": "Sayings of the Century",
                "price": 8.95
            },
            {
                "category": "fiction",
                "author": "Evelyn Waugh",
                "title": "Sword of Honour",
                "price": 12.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    }
}"""

class JsonAssertionsTest : StringSpec({

  "test json equality" {

    val json1 = """ { "name" : "sam", "location" : "london" } """
    val json2 = """ { "location": "london", "name" : "sam" } """
    val json3 = """ { "location": "chicago", "name" : "sam" } """

    json1.shouldMatchJson(json2)
    json1.shouldNotMatchJson(json3)
  }

  "test json path" {
    json.shouldContainJsonKey("$.store.bicycle")
    json.shouldContainJsonKey("$.store.book")
    json.shouldContainJsonKey("$.store.book[0]")
    json.shouldContainJsonKey("$.store.book[0].category")
    json.shouldContainJsonKey("$.store.book[1].price")

    json.shouldNotContainJsonKey("$.store.table")

    shouldThrow<AssertionError> {
      json.shouldContainJsonKey("$.store.table")
    }.message shouldBe """{
    "store": {
        "book": [
            {... should contain the path ${'$'}.store.table"""
  }

  "test json key value" {
    json.shouldContainJsonKeyValue("$.store.bicycle.color", "red")
    json.shouldContainJsonKeyValue("$.store.book[0].category", "reference")
    json.shouldContainJsonKeyValue("$.store.book[0].price", 8.95)
    json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")

    json.shouldNotContainJsonKeyValue("$.store.book[1].author", "JK Rowling")

    shouldThrow<AssertionError> {
      json.shouldContainJsonKeyValue("$.store.book[1].author", "JK Rowling")
    }.message shouldBe """{
    "store": {
        "book": [
            {... should contain the element ${'$'}.store.book[1].author = JK Rowling"""
  }
})