package org.bigloupe.web.chart.function;

import java.util.List;
import java.util.Map;

import org.bigloupe.ErrorCode;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.bigloupe.web.util.Utils;

import com.google.common.collect.ImmutableMap;

/**
 * Holder for all functions
 */
public class FunctionManager {

	Map<String, Class<? extends Function>> functions = new ImmutableMap.Builder<String, Class<? extends Function>>()
			.put("max", MaxFunction.class)
			.put("sum", SumFunction.class).build();




	public List execute(String function,
			List<Series> listSeriesWithFunction) {
		Class<? extends Function> clazz = functions.get(function);
		if (clazz == null)
			throw new ChartException(ErrorCode.UNKNOWN_FUNCTION, function);
		else {
			try {
				Function functionClass = (Function)Utils.callConstructor(clazz, new Object[] {});
				return functionClass.execute(listSeriesWithFunction);
			} catch (Exception e) {
				e.printStackTrace();
				String keys = "";
				for (Series series : listSeriesWithFunction) {
					if (keys.equals(""))
						keys = series.getKey();
					else
						keys = keys + "," + series.getKey();

				}
				throw new ChartException(ErrorCode.FUNCTION_ERROR, function, keys, e.getMessage());
			}
		}

	}
}
