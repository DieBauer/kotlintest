package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

fun <K> haveKey(key: K): Matcher<Map<out K, *>> = object : Matcher<Map<out K, *>> {
  override fun test(value: Map<out K, *>) = Result(value.containsKey(key),
      "Map should contain key $key",
      "Map should not contain key $key")
}

fun <K> haveKeys(vararg keys: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
  override fun test(value: Map<K, *>): Result {
    val passed = keys.all { value.containsKey(it) }
    return Result(passed,
        "Map did not contain the keys ${keys.joinToString(", ")}",
        "Map should not contain the keys ${keys.joinToString(", ")}")
  }
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>) = Result(value.containsValue(v),
      "Map should contain value $v",
      "Map should not contain value $v")
}

fun <V> haveValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>): Result {
    val passed = values.all { value.containsValue(it) }
    return Result(passed,
        "Map did not contain the values ${values.joinToString(", ")}",
        "Map should not contain the values ${values.joinToString(", ")}")
  }
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = Result(value[key] == v,
      "Map should contain mapping $key=$v but was $value",
      "Map should not contain mapping $key=$v but was $value")
}

fun <K, V> containAll(expected: Map<K, V>): Matcher<Map<K, V>> =
    MapContainsMatcher(expected, ignoreExtraKeys = true)

fun <K, V> containExactly(expected: Map<K, V>): Matcher<Map<K, V>> =
    MapContainsMatcher(expected)

class MapContainsMatcher<K, V>(
    private val expected: Map<K, V>,
    private val ignoreExtraKeys: Boolean = false
) : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>): Result {
    val diff = Diff.create(value, expected, ignoreExtraMapKeys = ignoreExtraKeys)
    val (expectMsg, negatedExpectMsg) = if (ignoreExtraKeys) {
      "should contain all of" to "should not contain all of"
    } else {
      "should be equal to" to "should not be equal to"
    }
    val (butMsg, negatedButMsg) = if (ignoreExtraKeys) {
      "but differs by" to "but contains"
    } else {
      "but differs by" to "but equals"
    }
    val failureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$expectMsg:
      |  ${stringify(expected)}
      |$butMsg:
      |${diff.toString(1)}
      |
      """.trimMargin()
    val negatedFailureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$negatedExpectMsg:
      |  ${stringify(expected)}
      |$negatedButMsg
      |
    """.trimMargin()
    return Result(
        diff.isEmpty(), failureMsg, negatedFailureMsg
    )
  }
}