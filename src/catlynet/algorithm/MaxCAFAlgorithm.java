/*
 * MaxCAFAlgorithm.java Copyright (C) 2019. Daniel H. Huson
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

import catlynet.model.Model;
import catlynet.model.MoleculeType;
import catlynet.model.Reaction;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * computes a CAF
 * Daniel Huson, 7.2019
 * Based on notes by Mike Steel
 */
public class MaxCAFAlgorithm extends ModelAlgorithmBase {
    /**
     * computes a CAF
     *
     * @param input
     * @param result
     */
    public void apply(Model input, Model result) {
        result.clear();
        result.setName("Max CAF");

        final Model expanded = input.getExpandedModel();
        final Set<Reaction> inputReactions = new TreeSet<>(expanded.getReactions());
        final Set<MoleculeType> inputFood = new TreeSet<>(expanded.getFoods());
        final Set<MoleculeType> mentionedFood = filterFood(inputFood, inputReactions);

        final Set<Reaction> startingReactions = filterReactions(mentionedFood, inputReactions);

        if (startingReactions.size() > 0) {
            final ArrayList<Set<Reaction>> reactions = new ArrayList<>();
            final ArrayList<Set<MoleculeType>> foods = new ArrayList<>();


            int i = 0;
            reactions.add(i, startingReactions);
            foods.add(i, mentionedFood);

            do {
                // System.err.println("i=" + i + ": " + Basic.toString(reactions.get(i), ", ") + " Food: " + Basic.toString(foods.get(i), " "));

                final Set<MoleculeType> nextFood = extendFood(foods.get(i), reactions.get(i));
                final Set<Reaction> nextReactions = filterReactions(nextFood, inputReactions);
                nextReactions.addAll(reactions.get(i));

                i++;
                reactions.add(i, nextReactions);
                foods.add(i, nextFood);
            }
            while (reactions.get(i).size() > reactions.get(i - 1).size());

            if (reactions.get(i).size() > 0) {
                result.getReactions().setAll(Model.compress(reactions.get(i)));
                result.getFoods().setAll(input.getFoods());
            }
        }
    }
}
