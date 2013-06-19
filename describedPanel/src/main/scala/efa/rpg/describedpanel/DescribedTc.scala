package efa.rpg.describedpanel

import efa.nb.tc.{Tc, TcProvider}

final class DescribedTc extends Tc[DescribedPane]

object DescribedTcProvider extends TcProvider[DescribedPane, DescribedTc]
