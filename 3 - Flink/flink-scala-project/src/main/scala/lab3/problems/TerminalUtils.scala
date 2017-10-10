package lab3.problems

sealed trait Terminal{def grid: Int}
case object Terminal_1 extends Terminal {val grid = 71436}
case object Terminal_2 extends Terminal {val grid = 71688}
case object Terminal_3 extends Terminal {val grid = 71191}
case object Terminal_4 extends Terminal {val grid = 70945}
case object Terminal_5 extends Terminal {val grid = 70190}
case object Terminal_6 extends Terminal {val grid = 70686}
case object Terminal_404 extends Terminal {val grid = -1}

object TerminalUtils {
  val terminals: Set[Terminal] = Set(Terminal_1, Terminal_2, Terminal_3, Terminal_4, Terminal_5, Terminal_6)

  def gridToTerminal(gridCell: Int): Terminal = {
    terminals.find(t => t.grid == gridCell) match {
      case Some(terminal) => terminal;
      case None => Terminal_404;
    }
  }

}
