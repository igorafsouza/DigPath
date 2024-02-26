getCellObjects().eachWithIndex{it, x ->

    if (it.getName() == null){

        def verCode = (x+1).toString()

        it.setName(verCode)

        }
}