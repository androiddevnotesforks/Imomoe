package com.skyd.imomoe.model.interfaces

// 接口版本，必须与APP的一致，APP才能应用
val interfaceVersion = "202206242210"

// 一般是接口没变，没有减少之前可用的方法，但多加了一些新的工具类或者方法功数据源调用。此时旧的数据源还可以使用
val coexistentInterfaceVersions: Array<String> = arrayOf(
    "202205211312"
)