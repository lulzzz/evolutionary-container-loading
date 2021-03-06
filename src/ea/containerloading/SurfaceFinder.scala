package ea.containerloading

import util.control.Breaks._

class SurfaceFinder(matrix: Array[Array[Int]], isFlat: Boolean = false) {

  private val matrixHeight = matrix.length
  private val matrixWidth = matrix(0).length
  private val helperMatrix = createHelperMatrix(isFlat)

  /**
   * O(n^2), with the help of Nikita Rybak, see link:
   * http://stackoverflow.com/questions/4656706/how-to-find-same-value-rectangular-areas-of-a-given-size-in-a-matrix-most-efficie/4657191#4657191
   */
  def findFlatSurfaces(surfaceWidth: Int, surfaceHeight: Int, maxValue: Int): List[Position2D] = {

    var resultPositions: List[Position2D] = Nil

    for (y <- 0 to matrixHeight - surfaceHeight) {
      var current_width = 0
      for (x <- 0 until matrixWidth) {
        if (helperMatrix(y)(x) < surfaceHeight - 1) {
          // this column has different numbers in it, no game
          current_width = 0
        } else if (current_width > 0 && matrix(y)(x) != matrix(y)(x - 1)) {
          // this column should consist of the same numbers as the one before
          current_width = 1
        } else {
          current_width += 1
          if (current_width >= surfaceWidth && matrix(y)(x) <= maxValue) {
            // TODO im Moment wird first fit benutzt -> nur 1 Ergebnis notwendig!
            //resultPositions ::= Position2D(x - surfaceWidth + 1, y)
            return List(Position2D(x - surfaceWidth + 1, y))
          }
        }
      }
    }

    return resultPositions
  }

  def updateArea(x: Int, y: Int, width: Int, height: Int, value: Int) = {
    // adjust matrix -> set new value in given region
    for {
      matrixX <- x until x + width
      matrixY <- y until y + height
    } {
      matrix(matrixY)(matrixX) = value
    }

    updateHelperMatrixCols(this.helperMatrix, x, x + width - 1, y + height - 1)
  }

  private def createHelperMatrix(isFlat: Boolean) = {
    val helperMatrix = Array.ofDim[Int](matrixHeight, matrixWidth)
    if (isFlat) {
      setupFlatHelperMatrix(helperMatrix)
    } else {
      updateHelperMatrixCols(helperMatrix, 0, matrixWidth - 1, matrixHeight - 1)
    }
    helperMatrix
  }

  private def setupFlatHelperMatrix(helperMatrix: Array[Array[Int]]) = {
    for {
      y <- 0 until matrixHeight
      x <- 0 until matrixWidth
    } {
      helperMatrix(y)(x) = matrixHeight - 1 - y
    }
  }

  private def updateHelperMatrixCols(helperMatrix: Array[Array[Int]], fromCol: Int, toCol: Int, fromRow: Int) = {
    val fromRowReal = if (fromRow == matrixHeight - 1) fromRow - 1 else fromRow
    for {
      y <- fromRowReal to 0 by -1
      x <- fromCol to toCol
    } {
      if (matrix(y)(x) == matrix(y + 1)(x)) {
        helperMatrix(y)(x) = helperMatrix(y + 1)(x) + 1
      } else {
        helperMatrix(y)(x) = 0
      }
    }
  }

}