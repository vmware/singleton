#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

class MultiIO
  def initialize(*targets)
    @targets = targets
  end

  def write(*args)
    @targets.each { |t| t.write(*args) }
  end

  def close
    @targets.each(&:close)
  end
end
