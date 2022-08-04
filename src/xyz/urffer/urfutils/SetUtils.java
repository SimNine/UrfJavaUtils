package xyz.urffer.urfutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils {
	
	/**
	 * Partitions a set into a list of sets, each of which is approximately the same size.
	 * @param <T>				The type of element in the original set
	 * @param originalSet				The set to split
	 * @param numPartitions		The number of partitions to split the original set into
	 * @return					A list of sets resulting from a split of the original set
	 */
	public static <T> List<Set<T>> partitionSet(Set<T> originalSet, int numPartitions) {
		ArrayList<Set<T>> partitionedSets = new ArrayList<Set<T>>(numPartitions);
		for (int i = 0; i < numPartitions; i++) {
		    partitionedSets.add(new HashSet<T>());
		}
		
		int index = 0;
		for (T object : originalSet) {
		    partitionedSets.get(index++ % numPartitions).add(object);
		}
		
		return partitionedSets;
	}

}
