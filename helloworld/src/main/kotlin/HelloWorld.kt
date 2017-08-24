import kotlinx.cinterop.*
import stdio.*

/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

fun main(args: Array<String>) {
    memScoped {
        val cwd = allocArray<ByteVar>(1024)
        println(getcwd(cwd, 1024)?.toKString())
    }
    try {
        goodArrayAccess()
    } catch (e: Throwable) {
        println(e.message ?: "something occurred")
    }
    try {
        badArrayAccess()
    } catch (e: Throwable) {
        println(e.message ?: "something occurred")
    }
}

fun badArrayAccess() {
    memScoped {
        val arr = intArrayOf(1, 2, 3)
                .toCValues()
                .getPointer(this)
        for (i in 0..3)
            println(arr[i])
    }

}

fun goodArrayAccess() {
    val arr = arrayOf(1, 2, 3)
    for (i in 0..3)
        println(arr[i])
}