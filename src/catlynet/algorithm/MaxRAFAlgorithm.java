/*
 * MaxRAFAlgorithm.java Copyright (C) 2019. Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catlynet.algorithm;

import catlynet.model.MoleculeType;
import catlynet.model.Reaction;
import catlynet.model.ReactionSystem;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * computes a maximal "reflexively autocatalytic F-generated reaction network" (RAF)
 * Daniel Huson, 7.2019
 * Based on notes by Mike Steel
 */
public class MaxRAFAlgorithm extends AlgorithmBase {
    /**
     * computes the RAF.
     * Ignore all inhibitions.
     *
     * @param input
     * @returns result, empty, it none exists
     */
    public ReactionSystem apply(ReactionSystem input) {
        final ReactionSystem result = new ReactionSystem();
        result.setName("Max RAF");

        final ReactionSystem expanded = input.getExpandedSystem();
        final Set<Reaction> inputReactions = new TreeSet<>(expanded.getReactions());
        final Set<MoleculeType> inputFood = new TreeSet<>(expanded.getFoods());

        if (inputReactions.size() > 0) {
            final ArrayList<Set<Reaction>> reactions = new ArrayList<>();
            final ArrayList<Set<MoleculeType>> molecules = new ArrayList<>();

            reactions.add(0, inputReactions);
            molecules.add(0, inputFood);

            int i = -1;
            do {
                i++;

                molecules.add(i + 1, computeClosure(inputFood, reactions.get(i)));
                reactions.add(i + 1, filterReactions(molecules.get(i + 1), reactions.get(i)));
            }
            while (reactions.get(i + 1).size() < reactions.get(i).size());

            if (reactions.get(i).size() > 0) {
                result.getReactions().setAll(reactions.get(i));
                result.getFoods().setAll(input.getFoods());
            }
        }
        return result;
    }
}
