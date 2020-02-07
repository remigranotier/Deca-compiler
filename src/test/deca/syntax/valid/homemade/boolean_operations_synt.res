`> [18, 0] Program
   +> ListDeclClass [List with 0 elements]
   `> [18, 0] Main
      +> ListDeclVar [List with 0 elements]
      `> ListInst [List with 9 elements]
         []> [19, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [19, 17] And
         ||         +> [19, 12] BooleanLiteral (true)
         ||         `> [19, 20] BooleanLiteral (true)
         []> [21, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [21, 17] Or
         ||         +> [21, 12] BooleanLiteral (true)
         ||         `> [21, 20] BooleanLiteral (true)
         []> [23, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [23, 12] Lower
         ||         +> [23, 12] Int (5)
         ||         `> [23, 16] Int (6)
         []> [25, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [25, 12] Greater
         ||         +> [25, 12] Int (5)
         ||         `> [25, 16] Int (6)
         []> [27, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [27, 12] LowerOrEqual
         ||         +> [27, 12] Int (5)
         ||         `> [27, 17] Int (6)
         []> [29, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [29, 12] GreaterOrEqual
         ||         +> [29, 12] Int (5)
         ||         `> [29, 17] Int (6)
         []> [31, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [31, 12] Equals
         ||         +> [31, 12] Int (5)
         ||         `> [31, 17] Int (4)
         []> [32, 4] Println
         ||  `> ListExpr [List with 1 elements]
         ||     []> [32, 12] NotEquals
         ||         +> [32, 12] Int (4)
         ||         `> [32, 17] Int (5)
         []> [35, 4] Println
             `> ListExpr [List with 1 elements]
                []> [35, 26] Or
                    +> [35, 18] And
                    |  +> [35, 12] BooleanLiteral (false)
                    |  `> [35, 21] BooleanLiteral (true)
                    `> [35, 30] Greater
                       +> [35, 30] Int (4)
                       `> [35, 34] Int (5)
