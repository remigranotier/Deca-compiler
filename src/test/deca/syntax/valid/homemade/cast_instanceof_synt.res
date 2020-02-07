`> [3, 0] Program
   +> ListDeclClass [List with 2 elements]
   |  []> [3, 0] DeclClass
   |  ||  +> [3, 6] Identifier (Ouaip)
   |  ||  +> [builtin] Identifier (Object)
   |  ||  `> ListDeclField [List with 1 elements]
   |  ||     []> [4, 8] DeclField
   |  ||         +> PUBLIC
   |  ||         +> [4, 4] Identifier (int)
   |  ||         +> [4, 8] Identifier (x)
   |  ||         `> [4, 10] Initialization
   |  ||            `> [4, 12] Int (3)
   |  ||  +> ListDeclMethod [List with 0 elements]
   |  []> [7, 0] DeclClass
   |      +> [7, 6] Identifier (Ouaipp)
   |      +> [7, 21] Identifier (Ouaip)
   |      `> ListDeclField [List with 1 elements]
   |         []> [8, 8] DeclField
   |             +> PUBLIC
   |             +> [8, 4] Identifier (int)
   |             +> [8, 8] Identifier (y)
   |             `> [8, 10] Initialization
   |                `> [8, 12] Int (4)
   |      +> ListDeclMethod [List with 0 elements]
   `> [11, 0] Main
      +> ListDeclVar [List with 4 elements]
      |  []> [12, 11] DeclVar
      |  ||  +> [12, 4] Identifier (Ouaipp)
      |  ||  +> [12, 11] Identifier (ouaipp)
      |  ||  `> [12, 18] Initialization
      |  ||     `> [12, 20] New
      |  ||        `> [12, 24] Identifier (Ouaipp)
      |  []> [13, 10] DeclVar
      |  ||  +> [13, 4] Identifier (Ouaip)
      |  ||  +> [13, 10] Identifier (ouaip)
      |  ||  `> [13, 16] Initialization
      |  ||     `> [13, 18] Cast
      |  ||        `> [13, 26] Identifier (ouaipp)
      |  []> [14, 8] DeclVar
      |  ||  +> [14, 4] Identifier (int)
      |  ||  +> [14, 8] Identifier (a)
      |  ||  `> [14, 10] Initialization
      |  ||     `> [14, 12] Int (2)
      |  []> [15, 10] DeclVar
      |      +> [15, 4] Identifier (float)
      |      +> [15, 10] Identifier (b)
      |      `> [15, 12] Initialization
      |         `> [15, 14] Cast
      |            `> [15, 36] Identifier (a)
      `> ListInst [List with 1 elements]
         []> [17, 8] IfThenElse
             +> [17, 15] InstanceOf
             |  +> [17, 8] Identifier (ouaipp)
             |  `> [17, 26] Identifier (Ouaip)
             +> ListInst [List with 1 elements]
             |  []> [18, 8] Println
             |      `> ListExpr [List with 1 elements]
             |         []> [18, 16] StringLiteral (yoyoyo)
             `> ListInst [List with 0 elements]
