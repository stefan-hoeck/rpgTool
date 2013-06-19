package efa.rpg.explorer

import efa.nb.tc.{TcProvider, Tc}

final class ExplorerTc extends Tc[ExplorerParams]

object ExplorerTcProvider extends TcProvider[ExplorerParams, ExplorerTc]

// vim: set ts=2 sw=2 et:
